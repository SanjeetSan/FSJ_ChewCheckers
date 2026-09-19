package com.smartnutrition.repository;

import com.smartnutrition.entity.NutritionScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionScoreRepository extends JpaRepository<NutritionScore, Long> {

    Optional<NutritionScore> findByMealId(Long mealId);

    List<NutritionScore> findTop7ByStudentIdOrderByCalculatedAtDesc(Long studentId);
}
