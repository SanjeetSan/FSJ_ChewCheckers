package com.smartnutrition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeRequest(
    @NotBlank(message = "Recipe title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    String title,

    String description,

    @NotBlank(message = "Ingredients cannot be blank")
    String ingredients,

    @NotBlank(message = "Instructions cannot be blank")
    String instructions
) {}
