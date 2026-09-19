package com.smartnutrition.dto.response;

import java.util.List;

public record StudentInsightResponse(
    String capacityStatus,
    String capacityRecommendation,
    String compartmentRecommendation,
    List<InsightDto> nutritionalInsights
) {
    public record InsightDto(
        String title,
        String desc,
        String badge,
        String badgeClass
    ) {}
}
