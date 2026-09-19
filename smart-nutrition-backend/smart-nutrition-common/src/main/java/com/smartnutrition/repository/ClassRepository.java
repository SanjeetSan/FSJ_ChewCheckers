package com.smartnutrition.repository;

import com.smartnutrition.entity.Class_;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class_, Long> {
    List<Class_> findByIsActiveTrue();
    List<Class_> findByTeacherEmailAndIsActiveTrue(String email);
    Optional<Class_> findByClassCode(String classCode);
    Optional<Class_> findByClassCodeIgnoreCase(String classCode);
}

