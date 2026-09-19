package com.smartnutrition.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StudentResponse(
    Long id,
    String name,
    String rollNumber,
    String studentCode,
    String gender,
    LocalDate dateOfBirth,
    BigDecimal weightKg,
    BigDecimal heightCm,
    String bloodGroup,
    String className,
    String schoolName,
    String teacherName,
    Long teacherId,
    Long parentId,
    String classCode,
    
    // Nutrition targets
    Integer dailyCalories,
    Integer dailyProtein,
    Integer dailyCarbs,
    Integer dailyFat,
    Integer dailyFiber,
    Integer lunchCalories,
    Integer lunchProtein,
    Integer lunchCarbs,
    Integer lunchFat,
    Integer lunchFiber,
    
    // Lunchbox profile
    BigDecimal boxLength,
    BigDecimal boxWidth,
    BigDecimal boxDepth,
    BigDecimal boxVolume,
    String boxShape,
    Integer boxCompartments
) {}
