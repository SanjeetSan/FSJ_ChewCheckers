package com.smartnutrition.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ClassReportResponse(
    String reportType,
    String classCode,
    String timePeriod,
    long totalMealsLogged,
    BigDecimal averageCalories,
    BigDecimal averageProteinG,
    BigDecimal averageCarbsG,
    BigDecimal averageFatG,
    BigDecimal averageFiberG,
    double averageLeftoverPercentage,
    List<String> topConsumedFoodItems,
    List<String> studentsNeedingAttentionNotes
) {}
