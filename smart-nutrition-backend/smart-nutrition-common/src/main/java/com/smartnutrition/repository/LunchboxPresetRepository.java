package com.smartnutrition.repository;

import com.smartnutrition.entity.LunchboxPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LunchboxPresetRepository extends JpaRepository<LunchboxPreset, Long> {

    List<LunchboxPreset> findByStudentIdOrderByIsDefaultDescCreatedAtDesc(Long studentId);

    Optional<LunchboxPreset> findByStudentIdAndIsDefaultTrue(Long studentId);

    Optional<LunchboxPreset> findByIdAndStudentId(Long id, Long studentId);

    @Modifying
    @Query("UPDATE LunchboxPreset p SET p.isDefault = false WHERE p.student.id = :studentId")
    void resetDefaultFlagsForStudent(@Param("studentId") Long studentId);
}
