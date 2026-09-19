package com.smartnutrition.dto.response;

public record SchoolResponse(
        Long id,
        String name,
        String address,
        String academicYear
) {}
