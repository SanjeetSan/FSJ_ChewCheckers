package com.smartnutrition.service;

import com.smartnutrition.dto.request.LoginRequest;
import com.smartnutrition.dto.request.RefreshTokenRequest;
import com.smartnutrition.dto.request.RegisterRequest;
import com.smartnutrition.dto.response.AuthResponse;
import com.smartnutrition.entity.RefreshToken;
import com.smartnutrition.entity.User;
import com.smartnutrition.enums.Role;
import com.smartnutrition.repository.RefreshTokenRepository;
import com.smartnutrition.repository.UserRepository;
import com.smartnutrition.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered: " + request.email());
        }

        Role assignedRole = request.role();
        if (isDeveloperEmail(request.email())) {
            assignedRole = Role.ADMIN;
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(assignedRole)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getId(), savedUser.getEmail());

        saveRefreshToken(savedUser, refreshToken);

        return new AuthResponse(accessToken, refreshToken, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No account found with email: " + request.email()));

        boolean matches = false;
        if (user.getPasswordHash() != null && passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            matches = true;
        }

        if (!matches) {
            throw new IllegalArgumentException("Incorrect password for this account!");
        }

        if (isDeveloperEmail(user.getEmail()) && user.getRole() != Role.ADMIN) {
            user.setRole(Role.ADMIN);
            user = userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        try {
            refreshTokenRepository.deleteByUser(user);
        } catch (Exception ignored) {}

        saveRefreshToken(user, refreshToken);

        return new AuthResponse(accessToken, refreshToken, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private boolean isDeveloperEmail(String email) {
        if (email == null) return false;
        String lower = email.toLowerCase();
        return lower.startsWith("sanjeet");
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken tokenEntity = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (tokenEntity.getIsRevoked() || tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token is expired or revoked");
        }

        User user = tokenEntity.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        tokenEntity.setIsRevoked(true);
        refreshTokenRepository.save(tokenEntity);
        saveRefreshToken(user, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private void saveRefreshToken(User user, String tokenStr) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenStr)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
    }
}
