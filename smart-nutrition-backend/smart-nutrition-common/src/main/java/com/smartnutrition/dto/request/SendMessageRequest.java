package com.smartnutrition.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
    @NotNull(message = "Receiver ID is required")
    Long receiverId,

    @NotBlank(message = "Message text cannot be blank")
    String messageText
) {}
