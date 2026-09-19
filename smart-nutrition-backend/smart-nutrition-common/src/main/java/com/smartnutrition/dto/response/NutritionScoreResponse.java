package com.smartnutrition.dto.response;

import com.smartnutrition.enums.NutritionClassification;

import java.math.BigDecimal;

public record NutritionScoreResponse(
    Long id,
    Long studentId,
    Long mealId,
    BigDecimal score,
    NutritionClassification classification,
    BigDecimal totalConsumedCalories,
    BigDecimal totalConsumedProteinG,
    BigDecimal totalConsumedCarbsG,
    BigDecimal totalConsumedFatG,
    BigDecimal totalConsumedFiberG,
    BigDecimal lunchCalorieTarget,
    BigDecimal lunchProteinTarget,
    String calculatedAt
) {}
