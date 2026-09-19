package com.smartnutrition.controller;

import com.smartnutrition.dto.request.UpdateProfileRequest;
import com.smartnutrition.dto.response.UserResponse;
import com.smartnutrition.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing user accounts and updating profile information")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get authenticated user's profile details")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update authenticated user's profile details (name, email, password)")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/picture")
    @Operation(summary = "Upload profile picture")
    public ResponseEntity<UserResponse> updateProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserResponse response = userService.updateProfilePicture(userDetails.getUsername(), file);
        return ResponseEntity.ok(response);
    }
}
