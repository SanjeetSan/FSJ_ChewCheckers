package com.smartnutrition.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    String name,

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password,

    String mobileNumber,

    String address
) {}
