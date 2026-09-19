package com.smartnutrition.entity;

import com.smartnutrition.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "mobile_number", length = 30)
    private String mobileNumber;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "profile_picture_path", length = 255)
    private String profilePicturePath;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    public User() {}

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public Boolean getIsActive() { return isActive; }
    public String getMobileNumber() { return mobileNumber; }
    public String getAddress() { return address; }
    public String getProfilePicturePath() { return profilePicturePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public Long getDeletedBy() { return deletedBy; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setProfilePicturePath(String profilePicturePath) { this.profilePicturePath = profilePicturePath; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setDeletedBy(Long deletedBy) { this.deletedBy = deletedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final User user = new User();
        public Builder name(String v) { user.name = v; return this; }
        public Builder email(String v) { user.email = v; return this; }
        public Builder passwordHash(String v) { user.passwordHash = v; return this; }
        public Builder role(Role v) { user.role = v; return this; }
        public Builder isActive(Boolean v) { user.isActive = v; return this; }
        public Builder mobileNumber(String v) { user.mobileNumber = v; return this; }
        public Builder address(String v) { user.address = v; return this; }
        public Builder profilePicturePath(String v) { user.profilePicturePath = v; return this; }
        public User build() { return user; }
    }
}
