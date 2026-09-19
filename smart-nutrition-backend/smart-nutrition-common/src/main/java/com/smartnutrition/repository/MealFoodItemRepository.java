package com.smartnutrition.repository;

import com.smartnutrition.entity.MealFoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealFoodItemRepository extends JpaRepository<MealFoodItem, Long> {

    List<MealFoodItem> findByMealId(Long mealId);
}
