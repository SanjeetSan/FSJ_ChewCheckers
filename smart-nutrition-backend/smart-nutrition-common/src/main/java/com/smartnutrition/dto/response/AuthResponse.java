package com.smartnutrition.dto.response;

import com.smartnutrition.enums.Role;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long userId,
    String name,
    String email,
    Role role
) {
    public AuthResponse(String accessToken, String refreshToken, Long userId, String name, String email, Role role) {
        this(accessToken, refreshToken, "Bearer", userId, name, email, role);
    }
}
