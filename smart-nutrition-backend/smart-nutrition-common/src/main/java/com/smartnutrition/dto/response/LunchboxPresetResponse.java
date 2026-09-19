package com.smartnutrition.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LunchboxPresetResponse(
    Long id,
    Long studentId,
    String studentName,
    String presetName,
    BigDecimal lengthCm,
    BigDecimal widthCm,
    BigDecimal heightCm,
    BigDecimal volumeCm3,
    String notes,
    Boolean isDefault,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
