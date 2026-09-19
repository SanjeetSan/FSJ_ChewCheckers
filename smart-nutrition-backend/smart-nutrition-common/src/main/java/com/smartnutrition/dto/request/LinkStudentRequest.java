package com.smartnutrition.dto.request;

import com.smartnutrition.enums.Relationship;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LinkStudentRequest(
    @NotBlank(message = "Student code is required")
    String studentCode,

    @NotNull(message = "Relationship is required (MOTHER, FATHER, GUARDIAN)")
    Relationship relationship,

    Boolean isPrimary
) {}
