package com.smartnutrition.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record QuickConsumptionRequest(
    @NotNull(message = "Meal ID is required")
    Long mealId,

    @NotNull(message = "Consumption percentage is required")
    BigDecimal overallConsumptionPercentage
) {}
