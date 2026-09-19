package com.smartnutrition.dto.response;

import com.smartnutrition.enums.Role;

public record UserResponse(
    Long id,
    String name,
    String email,
    Role role,
    String mobileNumber,
    String address,
    String profilePicturePath
) {
    public UserResponse(Long id, String name, String email, Role role) {
        this(id, name, email, role, null, null, null);
    }
}
