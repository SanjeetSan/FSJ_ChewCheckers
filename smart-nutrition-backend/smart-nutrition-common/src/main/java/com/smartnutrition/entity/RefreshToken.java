package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken() {}

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getToken() { return token; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public Boolean getIsRevoked() { return isRevoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setToken(String token) { this.token = token; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public void setIsRevoked(Boolean isRevoked) { this.isRevoked = isRevoked; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final RefreshToken rt = new RefreshToken();
        public Builder user(User v) { rt.user = v; return this; }
        public Builder token(String v) { rt.token = v; return this; }
        public Builder expiryDate(LocalDateTime v) { rt.expiryDate = v; return this; }
        public Builder isRevoked(Boolean v) { rt.isRevoked = v; return this; }
        public RefreshToken build() { return rt; }
    }
}
