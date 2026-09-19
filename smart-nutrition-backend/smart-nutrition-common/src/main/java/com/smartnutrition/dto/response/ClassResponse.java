package com.smartnutrition.dto.response;

public record ClassResponse(
    Long id,
    String className,
    String section,
    String academicYear,
    String classCode,
    String schoolName
) {}
