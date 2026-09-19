package com.smartnutrition.dto.response;

import com.smartnutrition.dto.request.FoodItemDto;
import com.smartnutrition.enums.MealStatus;

import java.time.LocalDate;
import java.util.List;

public record MealResponse(
    Long id,
    Long studentId,
    LocalDate mealDate,
    String preMealImageUrl,
    String postMealImageUrl,
    java.math.BigDecimal boxLength,
    java.math.BigDecimal boxWidth,
    java.math.BigDecimal boxHeight,
    Long lunchboxPresetId,
    String lunchboxPresetName,
    MealStatus status,
    List<FoodItemDto> foodItems
) {}
