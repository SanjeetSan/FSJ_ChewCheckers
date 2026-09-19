package com.smartnutrition.service;

import com.smartnutrition.dto.request.CreateStudentRequest;
import com.smartnutrition.dto.request.LinkStudentRequest;
import com.smartnutrition.dto.response.ClassResponse;
import com.smartnutrition.dto.response.StudentResponse;
import com.smartnutrition.entity.Class_;
import com.smartnutrition.entity.ParentStudent;
import com.smartnutrition.entity.Student;
import com.smartnutrition.entity.User;
import com.smartnutrition.entity.School;
import com.smartnutrition.repository.ClassRepository;
import com.smartnutrition.repository.ParentStudentRepository;
import com.smartnutrition.repository.StudentRepository;
import com.smartnutrition.repository.UserRepository;
import com.smartnutrition.repository.SchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ParentService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ParentStudentRepository parentStudentRepository;
    private final ClassRepository classRepository;
    private final SchoolRepository schoolRepository;

    @Value("${nutrition.lunch.allocation.ratio:0.35}")
    private double lunchAllocationRatio;

    public ParentService(UserRepository userRepository,
                         StudentRepository studentRepository,
                         ParentStudentRepository parentStudentRepository,
                         ClassRepository classRepository,
                         SchoolRepository schoolRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.parentStudentRepository = parentStudentRepository;
        this.classRepository = classRepository;
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    public StudentResponse createAndLinkStudent(String parentEmail, CreateStudentRequest request) {
        User parent = (parentEmail != null) ? userRepository.findByEmail(parentEmail).orElse(null) : null;
        if (parent == null) {
            parent = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.PARENT)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Class_ studentClass = classRepository.findByClassCodeIgnoreCase(request.classCode().trim())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with code: " + request.classCode()));

        // Generate unique student code (e.g. STU-123456)
        String studentCode;
        do {
            studentCode = "STU-" + String.format("%06d", (int)(Math.random() * 1000000));
        } while (studentRepository.findByStudentCode(studentCode).isPresent());

        School school = null;
        if (request.schoolId() != null) {
            school = schoolRepository.findById(request.schoolId()).orElse(null);
        }
        if (school == null) {
            school = studentClass.getSchool();
        }

        Student student = Student.builder()
                .name(request.name())
                .rollNumber(request.rollNumber())
                .studentCode(studentCode)
                .gender(request.gender())
                .dateOfBirth(request.dateOfBirth())
                .weightKg(request.weightKg())
                .heightCm(request.heightCm())
                .bloodGroup(request.bloodGroup())
                .studentClass(studentClass)
                .school(school)
                .isActive(true)
                .build();


        calculateAndSaveTargets(student);
        student = studentRepository.save(student);

        ParentStudent parentStudent = ParentStudent.builder()
                .parent(parent)
                .student(student)
                .relationship(request.relationship())
                .isPrimary(true)
                .build();

        parentStudentRepository.save(parentStudent);

        return mapToStudentResponse(student);
    }

    @Transactional
    public StudentResponse linkStudent(String parentEmail, LinkStudentRequest request) {
        User parent = userRepository.findByEmail(parentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

        Student student = studentRepository.findByStudentCode(request.studentCode())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with code: " + request.studentCode()));

        if (parentStudentRepository.existsByParentAndStudent(parent, student)) {
            throw new IllegalArgumentException("Student is already linked to your account");
        }

        ParentStudent parentStudent = ParentStudent.builder()
                .parent(parent)
                .student(student)
                .relationship(request.relationship())
                .isPrimary(request.isPrimary() != null ? request.isPrimary() : true)
                .build();

        parentStudentRepository.save(parentStudent);

        return mapToStudentResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getLinkedStudents(String parentEmail) {
        User parent = userRepository.findByEmail(parentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

        List<ParentStudent> links = parentStudentRepository.findByParentId(parent.getId());
        return links.stream()
                .map(link -> mapToStudentResponse(link.getStudent()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getActiveClasses() {
        return classRepository.findByIsActiveTrue().stream()
                .map(c -> new ClassResponse(
                        c.getId(),
                        c.getClassName(),
                        c.getSection(),
                        c.getAcademicYear(),
                        c.getClassCode(),
                        c.getSchool() != null ? c.getSchool().getName() : "N/A"
                ))
                .toList();
    }

    @Transactional
    public StudentResponse updateStudentClass(String parentEmail, Long studentId, String classCode) {
        User parent = (parentEmail != null) ? userRepository.findByEmail(parentEmail).orElse(null) : null;
        if (parent == null) {
            parent = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.PARENT)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!parentStudentRepository.existsByParentAndStudent(parent, student)) {
            throw new IllegalArgumentException("Student is not linked to your account");
        }

        if (classCode == null || classCode.trim().isEmpty() || "null".equalsIgnoreCase(classCode.trim())) {
            student.setStudentClass(null);
            student.setSchool(null);
        } else {
            Class_ studentClass = classRepository.findByClassCodeIgnoreCase(classCode.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Class not found with code: " + classCode));
            student.setStudentClass(studentClass);
            student.setSchool(studentClass.getSchool());
        }

        calculateAndSaveTargets(student);
        Student updated = studentRepository.save(student);

        return mapToStudentResponse(updated);
    }

    @Transactional
    public StudentResponse updateStudent(String parentEmail, Long studentId, CreateStudentRequest request) {
        User parent = (parentEmail != null) ? userRepository.findByEmail(parentEmail).orElse(null) : null;
        if (parent == null) {
            parent = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.PARENT)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!parentStudentRepository.existsByParentAndStudent(parent, student)) {
            throw new IllegalArgumentException("Student is not linked to your account");
        }

        if (request.name() != null && !request.name().isBlank()) {
            student.setName(request.name().trim());
        }
        if (request.rollNumber() != null) {
            student.setRollNumber(request.rollNumber().trim());
        }
        if (request.gender() != null) {
            student.setGender(request.gender());
        }
        if (request.dateOfBirth() != null) {
            student.setDateOfBirth(request.dateOfBirth());
        }
        if (request.weightKg() != null) {
            student.setWeightKg(request.weightKg());
        }
        if (request.heightCm() != null) {
            student.setHeightCm(request.heightCm());
        }
        if (request.bloodGroup() != null) {
            student.setBloodGroup(request.bloodGroup().trim());
        }

        if (request.classCode() != null && !request.classCode().isBlank() && !"null".equalsIgnoreCase(request.classCode().trim())) {
            Class_ studentClass = classRepository.findByClassCodeIgnoreCase(request.classCode().trim()).orElse(null);
            if (studentClass != null) {
                student.setStudentClass(studentClass);
                student.setSchool(studentClass.getSchool());
            }
        }

        calculateAndSaveTargets(student);
        Student updated = studentRepository.save(student);

        return mapToStudentResponse(updated);
    }

    @Transactional
    public StudentResponse updateStudentLunchbox(String parentEmail, Long studentId, BigDecimal length, BigDecimal width, BigDecimal depth, String shape, Integer compartments) {
        User parent = userRepository.findByEmail(parentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Parent user not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!parentStudentRepository.existsByParentAndStudent(parent, student)) {
            throw new IllegalArgumentException("Student is not linked to your account");
        }

        student.setBoxLength(length);
        student.setBoxWidth(width);
        student.setBoxDepth(depth);
        student.setBoxShape(shape);
        student.setBoxCompartments(compartments);

        if (length != null && width != null && depth != null) {
            BigDecimal vol;
            if ("circular".equalsIgnoreCase(shape)) {
                double r = length.doubleValue() / 2.0;
                double h = depth.doubleValue();
                double volVal = Math.PI * r * r * h;
                vol = BigDecimal.valueOf(volVal).setScale(2, RoundingMode.HALF_UP);
            } else {
                vol = length.multiply(width).multiply(depth).setScale(2, RoundingMode.HALF_UP);
            }
            student.setBoxVolume(vol);
        } else {
            student.setBoxVolume(null);
        }

        calculateAndSaveTargets(student);
        Student updated = studentRepository.save(student);
        return mapToStudentResponse(updated);
    }

    @Transactional
    public void deleteStudent(String parentEmail, Long studentId) {
        User parent = (parentEmail != null) ? userRepository.findByEmail(parentEmail).orElse(null) : null;
        if (parent == null) {
            parent = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.PARENT)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        parentStudentRepository.deleteByParentAndStudent(parent, student);
        student.setIsActive(false);
        student.setStudentClass(null);
        studentRepository.save(student);
    }

    private void calculateAndSaveTargets(Student student) {
        if (student.getDateOfBirth() == null || student.getWeightKg() == null || student.getHeightCm() == null) {
            return;
        }
        int age = java.time.Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        if (age < 1) age = 8;
        
        double weight = student.getWeightKg().doubleValue();
        double height = student.getHeightCm().doubleValue();
        
        double bmr = 10.0 * weight + 6.25 * height - 5.0 * age;
        if ("Female".equalsIgnoreCase(student.getGender())) {
            bmr -= 161.0;
        } else {
            bmr += 5.0;
        }
        
        int dailyCal = (int) Math.round(bmr * 1.35);
        int dailyProt = (int) Math.round(weight * 0.9);
        int dailyFiber = (int) Math.round((dailyCal / 1000.0) * 14.0);
        int dailyCarbs = (int) Math.round((dailyCal * 0.55) / 4.0);
        int dailyFat = (int) Math.round((dailyCal * 0.30) / 9.0);
        
        student.setDailyCalories(dailyCal);
        student.setDailyProtein(dailyProt);
        student.setDailyCarbs(dailyCarbs);
        student.setDailyFat(dailyFat);
        student.setDailyFiber(dailyFiber);
        
        student.setLunchCalories((int) Math.round(dailyCal * lunchAllocationRatio));
        student.setLunchProtein((int) Math.round(dailyProt * lunchAllocationRatio));
        student.setLunchCarbs((int) Math.round(dailyCarbs * lunchAllocationRatio));
        student.setLunchFat((int) Math.round(dailyFat * lunchAllocationRatio));
        student.setLunchFiber((int) Math.round(dailyFiber * lunchAllocationRatio));
    }

    private StudentResponse mapToStudentResponse(Student student) {
        String teacherName = "N/A";
        Long teacherId = null;
        if (student.getStudentClass() != null && student.getStudentClass().getTeacher() != null) {
            teacherName = student.getStudentClass().getTeacher().getName();
            teacherId = student.getStudentClass().getTeacher().getId();
        }

        Long parentId = null;
        List<ParentStudent> parentLinks = parentStudentRepository.findByStudentId(student.getId());
        if (parentLinks != null && !parentLinks.isEmpty()) {
            parentId = parentLinks.get(0).getParent().getId();
        }

        if (student.getDailyCalories() == null && student.getDateOfBirth() != null) {
            calculateAndSaveTargets(student);
        }

        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getRollNumber(),
                student.getStudentCode(),
                student.getGender(),
                student.getDateOfBirth(),
                student.getWeightKg(),
                student.getHeightCm(),
                student.getBloodGroup(),
                student.getStudentClass() != null ? student.getStudentClass().getClassName() + " " + student.getStudentClass().getSection() : "N/A",
                student.getSchool() != null ? student.getSchool().getName() : "N/A",
                teacherName,
                teacherId,
                parentId,
                student.getStudentClass() != null ? student.getStudentClass().getClassCode() : "N/A",
                student.getDailyCalories(),
                student.getDailyProtein(),
                student.getDailyCarbs(),
                student.getDailyFat(),
                student.getDailyFiber(),
                student.getLunchCalories(),
                student.getLunchProtein(),
                student.getLunchCarbs(),
                student.getLunchFat(),
                student.getLunchFiber(),
                student.getBoxLength(),
                student.getBoxWidth(),
                student.getBoxDepth(),
                student.getBoxVolume(),
                student.getBoxShape(),
                student.getBoxCompartments()
        );
    }
}
