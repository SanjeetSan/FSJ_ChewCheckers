package com.smartnutrition.dto.request;

import com.smartnutrition.enums.FoodItemSource;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record FoodItemDto(
    @NotBlank(message = "Food name is required")
    String foodName,

    String quantity,       // e.g. "150g" or "1 cup"
    String cookingNote,    // Manual recipe note from parent
    BigDecimal calories,
    BigDecimal proteinG,
    BigDecimal carbsG,
    BigDecimal fatG,
    BigDecimal fiberG,
    FoodItemSource source,
    BigDecimal consumptionPercentage,
    BigDecimal consumedCalories,
    BigDecimal consumedProteinG,
    BigDecimal consumedCarbsG,
    BigDecimal consumedFatG,
    BigDecimal consumedFiberG
) {
    public FoodItemDto(String foodName, String quantity, String cookingNote, BigDecimal calories,
                       BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG, BigDecimal fiberG,
                       FoodItemSource source) {
        this(foodName, quantity, cookingNote, calories, proteinG, carbsG, fatG, fiberG, source, null, null, null, null, null, null);
    }

    public FoodItemDto(String foodName, String quantity, String cookingNote, BigDecimal calories,
                       BigDecimal proteinG, BigDecimal carbsG, BigDecimal fatG, BigDecimal fiberG,
                       FoodItemSource source, BigDecimal consumptionPercentage) {
        this(foodName, quantity, cookingNote, calories, proteinG, carbsG, fatG, fiberG, source, consumptionPercentage, null, null, null, null, null);
    }
}

