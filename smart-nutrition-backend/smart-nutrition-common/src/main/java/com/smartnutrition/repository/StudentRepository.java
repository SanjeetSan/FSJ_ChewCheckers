package com.smartnutrition.repository;

import com.smartnutrition.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentCode(String studentCode);

    boolean existsByStudentCode(String studentCode);

    java.util.List<Student> findByStudentClassId(Long classId);

    java.util.List<Student> findByStudentClassClassCode(String classCode);

    java.util.List<Student> findByStudentClassIsNullAndIsActiveTrue();

    java.util.List<Student> findByIsActiveTrue();

    @org.springframework.data.jpa.repository.Query("SELECT s FROM Student s WHERE s.isActive = true AND (LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    java.util.List<Student> findEligibleStudents(@org.springframework.data.repository.query.Param("query") String query);

    default java.util.List<Student> findByClassCode(String classCode) {
        return findByStudentClassClassCode(classCode);
    }
}

