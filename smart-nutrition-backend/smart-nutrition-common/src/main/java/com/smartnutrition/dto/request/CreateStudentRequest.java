package com.smartnutrition.dto.request;

import com.smartnutrition.enums.Relationship;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateStudentRequest(
    @NotBlank(message = "Student name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name,

    @Size(max = 20, message = "Roll number cannot exceed 20 characters")
    String rollNumber,

    @Size(max = 10, message = "Gender cannot exceed 10 characters")
    String gender,

    LocalDate dateOfBirth,

    BigDecimal weightKg,

    BigDecimal heightCm,

    @Size(max = 20, message = "Blood group cannot exceed 20 characters")
    String bloodGroup,

    @NotBlank(message = "Class code is required")
    String classCode,

    Long schoolId,

    @NotNull(message = "Relationship is required (MOTHER, FATHER, GUARDIAN)")
    Relationship relationship

) {}
