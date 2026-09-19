package com.smartnutrition.service;

import com.smartnutrition.dto.request.UpdateUserRoleRequest;
import com.smartnutrition.dto.response.AdminUserResponse;
import com.smartnutrition.entity.User;
import com.smartnutrition.enums.Role;
import com.smartnutrition.repository.MealRepository;
import com.smartnutrition.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final JdbcTemplate jdbcTemplate;

    public AdminService(UserRepository userRepository, MealRepository mealRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getIsActive() == null || Boolean.TRUE.equals(u.getIsActive()))
                .map(u -> new AdminUserResponse(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRole(),
                        u.getIsActive(),
                        u.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public AdminUserResponse updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role currentRole = user.getRole();
        Role newRole = request.role();

        if ((currentRole == Role.PARENT || newRole == Role.PARENT) && currentRole != newRole) {
            throw new IllegalArgumentException("Parent accounts cannot be converted through the Admin panel.");
        }

        user.setRole(newRole);
        User updated = userRepository.save(user);

        return new AdminUserResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getRole(),
                updated.getIsActive(),
                updated.getCreatedAt()
        );
    }

    @Transactional
    public AdminUserResponse updateUser(Long userId, Map<String, Object> request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (request.containsKey("name") && request.get("name") != null) {
            user.setName(request.get("name").toString());
        }
        if (request.containsKey("email") && request.get("email") != null) {
            user.setEmail(request.get("email").toString());
        }
        if (request.containsKey("role") && request.get("role") != null) {
            try {
                Role newRole = Role.valueOf(request.get("role").toString().toUpperCase());
                Role currentRole = user.getRole();
                if ((currentRole == Role.PARENT || newRole == Role.PARENT) && currentRole != newRole) {
                    throw new IllegalArgumentException("Parent accounts cannot be converted through the Admin panel.");
                }
                user.setRole(newRole);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {}
        }
        User updated = userRepository.save(user);

        return new AdminUserResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                updated.getRole(),
                updated.getIsActive(),
                updated.getCreatedAt()
        );
    }

    @Transactional
    public void deleteUser(Long userId) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        try { jdbcTemplate.update("DELETE FROM refresh_tokens WHERE user_id = ?", userId); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM messages WHERE sender_id = ? OR receiver_id = ?", userId, userId); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM friendships WHERE user_id = ? OR friend_id = ?", userId, userId); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM meals WHERE parent_id = ? OR teacher_id = ?", userId, userId); } catch (Exception e) {}
        try { jdbcTemplate.update("DELETE FROM users WHERE id = ?", userId); } catch (Exception e) {}
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    public Map<String, Object> getSystemHealth() {
        long totalUsers = userRepository.count();
        long totalMeals = mealRepository.count();

        return Map.of(
                "status", "UP",
                "database", "MySQL (smart_nutrition_db)",
                "totalRegisteredUsers", totalUsers,
                "totalLoggedMeals", totalMeals,
                "environment", "Development / School Admin Operations"
        );
    }
}
