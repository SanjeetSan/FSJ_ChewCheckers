package com.smartnutrition.dto.request;

import com.smartnutrition.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
    @NotNull(message = "Role is required")
    Role role
) {}
