package com.smartnutrition.dto.response;

import com.smartnutrition.enums.Role;
import java.time.LocalDateTime;

public record AdminUserResponse(
    Long id,
    String name,
    String email,
    Role role,
    Boolean isActive,
    LocalDateTime createdAt
) {}
