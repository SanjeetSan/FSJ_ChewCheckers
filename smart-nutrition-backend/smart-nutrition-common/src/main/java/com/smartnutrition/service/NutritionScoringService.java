package com.smartnutrition.service;

import com.smartnutrition.entity.Meal;
import com.smartnutrition.entity.MealFoodItem;
import com.smartnutrition.entity.NutritionScore;
import com.smartnutrition.entity.Student;
import com.smartnutrition.enums.NutritionClassification;
import com.smartnutrition.repository.NutritionScoreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class NutritionScoringService {

    private final NutritionScoreRepository nutritionScoreRepository;

    @Value("${nutrition.lunch.allocation-ratio:0.35}")
    private BigDecimal lunchAllocationRatio;

    public NutritionScoringService(NutritionScoreRepository nutritionScoreRepository) {
        this.nutritionScoreRepository = nutritionScoreRepository;
    }

    @Transactional
    public NutritionScore calculateAndSaveScore(Student student, Meal meal, List<MealFoodItem> foodItems) {
        BigDecimal dailyCalorieTarget = student.getDailyCalories() != null 
                ? new BigDecimal(student.getDailyCalories()) 
                : (student.getWeightKg() != null && student.getWeightKg().compareTo(BigDecimal.ZERO) > 0 
                        ? student.getWeightKg().multiply(new BigDecimal("60.00")) 
                        : new BigDecimal("1800.00"));
        BigDecimal dailyProteinTarget = student.getDailyProtein() != null 
                ? new BigDecimal(student.getDailyProtein()) 
                : new BigDecimal("45.00");
        BigDecimal dailyFiberTarget = student.getDailyFiber() != null 
                ? new BigDecimal(student.getDailyFiber()) 
                : new BigDecimal("25.00");

        BigDecimal ratio = lunchAllocationRatio != null ? lunchAllocationRatio : new BigDecimal("0.35");

        BigDecimal lunchCalorieTarget = student.getLunchCalories() != null 
                ? new BigDecimal(student.getLunchCalories()) 
                : dailyCalorieTarget.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lunchProteinTarget = student.getLunchProtein() != null 
                ? new BigDecimal(student.getLunchProtein()) 
                : dailyProteinTarget.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lunchFiberTarget   = student.getLunchFiber() != null 
                ? new BigDecimal(student.getLunchFiber()) 
                : dailyFiberTarget.multiply(ratio).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCalories = BigDecimal.ZERO;
        BigDecimal totalProtein  = BigDecimal.ZERO;
        BigDecimal totalCarbs    = BigDecimal.ZERO;
        BigDecimal totalFat      = BigDecimal.ZERO;
        BigDecimal totalFiber    = BigDecimal.ZERO;

        for (MealFoodItem item : foodItems) {
            if (item.getConsumedCalories() != null) totalCalories = totalCalories.add(item.getConsumedCalories());
            if (item.getConsumedProteinG() != null) totalProtein  = totalProtein.add(item.getConsumedProteinG());
            if (item.getConsumedCarbsG() != null)   totalCarbs    = totalCarbs.add(item.getConsumedCarbsG());
            if (item.getConsumedFatG() != null)      totalFat      = totalFat.add(item.getConsumedFatG());
            if (item.getConsumedFiberG() != null)    totalFiber    = totalFiber.add(item.getConsumedFiberG());
        }

        BigDecimal calorieRatio = lunchCalorieTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalCalories.divide(lunchCalorieTarget, 4, RoundingMode.HALF_UP).min(BigDecimal.ONE)
                : BigDecimal.ZERO;
        BigDecimal proteinRatio = lunchProteinTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalProtein.divide(lunchProteinTarget, 4, RoundingMode.HALF_UP).min(BigDecimal.ONE)
                : BigDecimal.ZERO;
        BigDecimal fiberRatio = lunchFiberTarget.compareTo(BigDecimal.ZERO) > 0
                ? totalFiber.divide(lunchFiberTarget, 4, RoundingMode.HALF_UP).min(BigDecimal.ONE)
                : BigDecimal.ZERO;

        BigDecimal rawScore = calorieRatio.multiply(new BigDecimal("40.00"))
                .add(proteinRatio.multiply(new BigDecimal("30.00")))
                .add(fiberRatio.multiply(new BigDecimal("30.00")))
                .setScale(2, RoundingMode.HALF_UP);

        NutritionClassification classification;
        if (rawScore.compareTo(new BigDecimal("40.00")) <= 0) {
            classification = NutritionClassification.POOR;
        } else if (rawScore.compareTo(new BigDecimal("70.00")) <= 0) {
            classification = NutritionClassification.AVERAGE;
        } else {
            classification = NutritionClassification.GOOD;
        }

        NutritionScore nutritionScore = NutritionScore.builder()
                .student(student)
                .meal(meal)
                .score(rawScore)
                .classification(classification)
                .totalConsumedCalories(totalCalories)
                .totalConsumedProteinG(totalProtein)
                .totalConsumedCarbsG(totalCarbs)
                .totalConsumedFatG(totalFat)
                .totalConsumedFiberG(totalFiber)
                .lunchCalorieTarget(lunchCalorieTarget)
                .lunchProteinTarget(lunchProteinTarget)
                .build();

        return nutritionScoreRepository.save(nutritionScore);
    }
}
