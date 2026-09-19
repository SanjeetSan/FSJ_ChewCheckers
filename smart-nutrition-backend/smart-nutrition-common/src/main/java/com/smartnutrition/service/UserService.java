package com.smartnutrition.service;

import com.smartnutrition.dto.request.UpdateProfileRequest;
import com.smartnutrition.dto.response.UserResponse;
import com.smartnutrition.entity.User;
import com.smartnutrition.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getMobileNumber(),
                user.getAddress(),
                user.getProfilePicturePath()
        );
    }

    @Transactional
    public UserResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseGet(() -> {
                    if (request.email() != null) {
                        return userRepository.findByEmail(request.email()).orElse(null);
                    }
                    return null;
                });

        if (user == null) {
            throw new IllegalArgumentException("User account not found for: " + currentEmail);
        }

        if (request.email() != null && !request.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new IllegalArgumentException("Email already in use by another account");
            }
            user.setEmail(request.email());
        }

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        if (request.mobileNumber() != null) {
            user.setMobileNumber(request.mobileNumber());
        }

        if (request.address() != null) {
            user.setAddress(request.address());
        }

        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getMobileNumber(),
                savedUser.getAddress(),
                savedUser.getProfilePicturePath()
        );
    }

    @Transactional
    public UserResponse updateProfilePicture(String email, MultipartFile file) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.findAll().stream().findFirst().orElse(null));

        if (user == null) {
            throw new IllegalArgumentException("User account not found");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "jpg";
        if (originalFilename != null && originalFilename.contains(".")) {
            int dotIndex = originalFilename.lastIndexOf('.');
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png") && !extension.equals("webp")) {
            throw new IllegalArgumentException("Only image files (jpg, jpeg, png, webp) are allowed.");
        }

        Path uploadPath = Paths.get("uploads");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = "avatar_" + user.getId() + "." + extension;
        Path targetLocation = uploadPath.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        user.setProfilePicturePath("/uploads/" + filename);
        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getMobileNumber(),
                savedUser.getAddress(),
                savedUser.getProfilePicturePath()
        );
    }
}
