package com.smartnutrition.dto.response;

public record RecipeResponse(
    Long id,
    String title,
    String description,
    String ingredients,
    String instructions,
    Long createdById,
    String createdByAuthor
) {}
