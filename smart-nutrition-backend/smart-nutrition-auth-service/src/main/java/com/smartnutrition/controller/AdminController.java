package com.smartnutrition.controller;

import com.smartnutrition.dto.request.UpdateUserRoleRequest;
import com.smartnutrition.dto.response.AdminUserResponse;
import com.smartnutrition.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartnutrition.entity.Holiday;
import com.smartnutrition.entity.Message;
import com.smartnutrition.entity.User;
import com.smartnutrition.enums.Role;
import com.smartnutrition.repository.HolidayRepository;
import com.smartnutrition.repository.MessageRepository;
import com.smartnutrition.repository.UserRepository;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin Operations", description = "Privileged management operations for developers and school administrators")
public class AdminController {

    private final AdminService adminService;
    private final HolidayRepository holidayRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public AdminController(AdminService adminService, HolidayRepository holidayRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.adminService = adminService;
        this.holidayRepository = holidayRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Retrieves all registered parents, teachers, and administrators in the system.")
    public ResponseEntity<List<AdminUserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}/roles")
    @Operation(summary = "Update user role", description = "Promotes or changes a user role (e.g. promoting a teacher to ADMIN / School Management).")
    public ResponseEntity<AdminUserResponse> updateUserRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRole(id, request));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user profile", description = "Updates user name, email, and role in MySQL database.")
    public ResponseEntity<AdminUserResponse> updateUser(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Deactivate/Delete user", description = "Deactivates a user account in the system.")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
    }

    @GetMapping("/system/health")
    @Operation(summary = "System health and metrics", description = "Returns system diagnostics for debugging and administrative monitoring.")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        return ResponseEntity.ok(adminService.getSystemHealth());
    }

    @GetMapping("/holidays")
    @Operation(summary = "Get all holidays")
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        return ResponseEntity.ok(holidayRepository.findAll());
    }

    @PostMapping("/holidays")
    @Operation(summary = "Create a new holiday")
    public ResponseEntity<Holiday> createHoliday(@RequestBody Holiday holiday) {
        Holiday saved = holidayRepository.save(holiday);
        notifyParentsAboutClosure(saved);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/holidays/{id}")
    @Operation(summary = "Update a holiday")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable("id") Long id, @RequestBody Holiday request) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found"));
        holiday.setName(request.getName());
        holiday.setStartDate(request.getStartDate());
        holiday.setEndDate(request.getEndDate());
        holiday.setDuration(request.getDuration());
        holiday.setStatus(request.getStatus());
        Holiday saved = holidayRepository.save(holiday);
        notifyParentsAboutClosure(saved);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/holidays/{id}")
    @Operation(summary = "Delete a holiday")
    public ResponseEntity<?> deleteHoliday(@PathVariable("id") Long id) {
        holidayRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Holiday deleted successfully"));
    }

    private void notifyParentsAboutClosure(Holiday holiday) {
        try {
            List<User> parents = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.PARENT && Boolean.TRUE.equals(u.getIsActive()))
                    .toList();

            User adminSender = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN && Boolean.TRUE.equals(u.getIsActive()))
                    .findFirst()
                    .orElse(null);

            if (adminSender == null || parents.isEmpty()) return;

            String dateStr = (holiday.getStartDate() != null && holiday.getStartDate().equals(holiday.getEndDate()))
                    ? holiday.getStartDate().toString()
                    : (holiday.getStartDate() + " to " + holiday.getEndDate());

            String messageText = "School Closure Notice:\nSchool will remain closed on " + dateStr + " (" + holiday.getName() + ").\nPlease ensure your child's nutritional intake is monitored at home during the closure period.";

            for (User parent : parents) {
                boolean exists = messageRepository.findChatHistory(adminSender.getId(), parent.getId()).stream()
                        .anyMatch(m -> messageText.equals(m.getMessageText()));

                if (!exists) {
                    Message noticeMsg = Message.builder()
                            .sender(adminSender)
                            .receiver(parent)
                            .messageText(messageText)
                            .build();
                    messageRepository.save(noticeMsg);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice dispatch warning: " + e.getMessage());
        }
    }
}
