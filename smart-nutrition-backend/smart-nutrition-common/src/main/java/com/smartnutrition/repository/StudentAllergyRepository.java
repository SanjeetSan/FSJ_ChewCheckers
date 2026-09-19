package com.smartnutrition.repository;

import com.smartnutrition.entity.StudentAllergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAllergyRepository extends JpaRepository<StudentAllergy, Long> {

    List<StudentAllergy> findByStudentId(Long studentId);
}
