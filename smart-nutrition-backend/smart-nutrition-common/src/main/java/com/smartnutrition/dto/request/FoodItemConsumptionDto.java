package com.smartnutrition.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FoodItemConsumptionDto(
    @NotBlank(message = "Food name is required")
    String foodName,

    @NotNull(message = "Consumed percentage is required")
    @DecimalMin(value = "0.0") @DecimalMax(value = "100.0")
    BigDecimal consumedPercentage
) {}
