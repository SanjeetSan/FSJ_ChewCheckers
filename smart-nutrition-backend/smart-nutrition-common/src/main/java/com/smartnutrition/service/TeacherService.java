package com.smartnutrition.service;

import com.smartnutrition.dto.response.ClassResponse;
import com.smartnutrition.dto.response.StudentResponse;
import com.smartnutrition.entity.Class_;
import com.smartnutrition.entity.Student;
import com.smartnutrition.entity.User;
import com.smartnutrition.repository.ClassRepository;
import com.smartnutrition.repository.StudentRepository;
import com.smartnutrition.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.smartnutrition.repository.ParentStudentRepository;
import com.smartnutrition.entity.ParentStudent;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final ParentStudentRepository parentStudentRepository;
    private final UserRepository userRepository;

    public TeacherService(ClassRepository classRepository,
                          StudentRepository studentRepository,
                          ParentStudentRepository parentStudentRepository,
                          UserRepository userRepository) {
        this.classRepository = classRepository;
        this.studentRepository = studentRepository;
        this.parentStudentRepository = parentStudentRepository;
        this.userRepository = userRepository;
    }

    private User findTeacher(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found");
        }
        User teacher = userRepository.findByEmail(identifier.trim())
                .or(() -> {
                    try {
                        Long userId = Long.parseLong(identifier.trim());
                        return userRepository.findById(userId);
                    } catch (NumberFormatException e) {
                        return Optional.empty();
                    }
                })
                .or(() -> {
                    if ("admin".equalsIgnoreCase(identifier.trim())) {
                        return userRepository.findAll().stream()
                                .filter(u -> u.getRole() == com.smartnutrition.enums.Role.TEACHER && Boolean.TRUE.equals(u.getIsActive()))
                                .findFirst();
                    }
                    return Optional.empty();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));

        if (teacher.getRole() != com.smartnutrition.enums.Role.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a teacher");
        }
        return teacher;
    }

    private void checkTeacherOwnsClass(User teacher, Class_ studentClass) {
        if (studentClass.getTeacher() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Class has no assigned teacher");
        }
        boolean matchesEmail = teacher.getEmail() != null && teacher.getEmail().equalsIgnoreCase(studentClass.getTeacher().getEmail());
        boolean matchesId = teacher.getId() != null && teacher.getId().equals(studentClass.getTeacher().getId());
        if (!matchesEmail && !matchesId) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: You are not assigned to this class");
        }
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getTeacherClasses(String identifier) {
        User teacher = findTeacher(identifier);
        return classRepository.findByTeacherEmailAndIsActiveTrue(teacher.getEmail()).stream()
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

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByClassCode(String classCode) {
        Class_ studentClass = classRepository.findByClassCodeIgnoreCase(classCode.trim())
                .orElseThrow(() -> new IllegalArgumentException("Class not found with code: " + classCode));

        return studentRepository.findByStudentClassId(studentClass.getId()).stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByClassCodeAndTeacher(String classCode, String teacherEmail) {
        User teacher = findTeacher(teacherEmail);

        Class_ studentClass = classRepository.findByClassCodeIgnoreCase(classCode.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found with code: " + classCode));

        checkTeacherOwnsClass(teacher, studentClass);

        return studentRepository.findByStudentClassId(studentClass.getId()).stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    @Transactional
    public StudentResponse linkStudent(Long studentId, String classCode, String teacherEmail) {
        User teacher = findTeacher(teacherEmail);

        Class_ studentClass = classRepository.findByClassCodeIgnoreCase(classCode.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found with code: " + classCode));

        checkTeacherOwnsClass(teacher, studentClass);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + studentId));

        if (!Boolean.TRUE.equals(student.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not active");
        }

        if (student.getStudentClass() != null && student.getStudentClass().getId().equals(studentClass.getId())) {
            return mapToStudentResponse(student);
        }

        student.setStudentClass(studentClass);
        if (studentClass.getSchool() != null) {
            student.setSchool(studentClass.getSchool());
        }
        Student saved = studentRepository.save(student);

        return mapToStudentResponse(saved);
    }

    @Transactional
    public void unlinkStudent(Long studentId, String classCode, String teacherEmail) {
        User teacher = findTeacher(teacherEmail);

        Class_ studentClass = classRepository.findByClassCodeIgnoreCase(classCode.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Class not found with code: " + classCode));

        checkTeacherOwnsClass(teacher, studentClass);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with ID: " + studentId));

        if (student.getStudentClass() == null || !student.getStudentClass().getId().equals(studentClass.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student does not belong to this class");
        }

        student.setStudentClass(null);
        student.setSchool(null);
        studentRepository.save(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getEligibleStudents(String query, String teacherEmail) {
        User teacher = findTeacher(teacherEmail);

        if (teacher.getRole() != com.smartnutrition.enums.Role.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User is not a teacher");
        }

        List<Student> eligibleStudents;
        if (query != null && !query.trim().isEmpty()) {
            eligibleStudents = studentRepository.findEligibleStudents(query.trim());
        } else {
            eligibleStudents = studentRepository.findByIsActiveTrue();
        }

        return eligibleStudents.stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    private StudentResponse mapToStudentResponse(Student s) {
        String teacherName = "N/A";
        Long teacherId = null;
        if (s.getStudentClass() != null && s.getStudentClass().getTeacher() != null) {
            teacherName = s.getStudentClass().getTeacher().getName();
            teacherId = s.getStudentClass().getTeacher().getId();
        }

        Long parentId = null;
        List<ParentStudent> parentLinks = parentStudentRepository.findByStudentId(s.getId());
        if (parentLinks != null && !parentLinks.isEmpty()) {
            parentId = parentLinks.get(0).getParent().getId();
        }

        return new StudentResponse(
                s.getId(),
                s.getName(),
                s.getRollNumber(),
                s.getStudentCode(),
                s.getGender(),
                s.getDateOfBirth(),
                s.getWeightKg(),
                s.getHeightCm(),
                s.getBloodGroup(),
                s.getStudentClass() != null ? s.getStudentClass().getClassName() + " " + s.getStudentClass().getSection() : "N/A",
                s.getSchool() != null ? s.getSchool().getName() : "N/A",
                teacherName,
                teacherId,
                parentId,
                s.getStudentClass() != null ? s.getStudentClass().getClassCode() : "N/A",
                s.getDailyCalories(),
                s.getDailyProtein(),
                s.getDailyCarbs(),
                s.getDailyFat(),
                s.getDailyFiber(),
                s.getLunchCalories(),
                s.getLunchProtein(),
                s.getLunchCarbs(),
                s.getLunchFat(),
                s.getLunchFiber(),
                s.getBoxLength(),
                s.getBoxWidth(),
                s.getBoxDepth(),
                s.getBoxVolume(),
                s.getBoxShape(),
                s.getBoxCompartments()
        );
    }

    @Transactional
    public ClassResponse generateNewClassCode(Long classId) {
        Class_ clazz = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found with ID: " + classId));

        String newCode;
        do {
            newCode = "CLS-" + String.format("%04d", (int)(Math.random() * 10000));
        } while (classRepository.findByClassCodeIgnoreCase(newCode).isPresent());

        clazz.setClassCode(newCode);
        Class_ saved = classRepository.save(clazz);

        return new ClassResponse(
                saved.getId(),
                saved.getClassName(),
                saved.getSection(),
                saved.getAcademicYear(),
                saved.getClassCode(),
                saved.getSchool() != null ? saved.getSchool().getName() : "N/A"
        );
    }
}
