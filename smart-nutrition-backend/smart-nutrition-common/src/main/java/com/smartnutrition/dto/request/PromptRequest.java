package com.smartnutrition.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PromptRequest(
    @NotBlank(message = "Message prompt cannot be empty")
    String message
) {}
