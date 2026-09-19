package com.smartnutrition.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LunchboxPresetRequest(
    @NotBlank(message = "Lunchbox preset name is required")
    String presetName,

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1", message = "Length must be greater than 0")
    BigDecimal lengthCm,

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1", message = "Width must be greater than 0")
    BigDecimal widthCm,

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.1", message = "Height must be greater than 0")
    BigDecimal heightCm,

    String notes,

    Boolean isDefault
) {}
