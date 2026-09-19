package com.smartnutrition.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Long senderId,
    String senderName,
    Long receiverId,
    String receiverName,
    String messageText,
    LocalDateTime sentAt
) {}
