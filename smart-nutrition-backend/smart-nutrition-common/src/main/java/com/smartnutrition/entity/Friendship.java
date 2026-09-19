package com.smartnutrition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "friendships",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id_1", "user_id_2"}))
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_1", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_2", nullable = false)
    private User user2;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, ACCEPTED

    public Friendship() {}

    public Long getId() { return id; }
    public User getUser1() { return user1; }
    public User getUser2() { return user2; }
    public String getStatus() { return status; }

    public void setUser1(User user1) { this.user1 = user1; }
    public void setUser2(User user2) { this.user2 = user2; }
    public void setStatus(String status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Friendship f = new Friendship();
        public Builder user1(User v) { f.user1 = v; return this; }
        public Builder user2(User v) { f.user2 = v; return this; }
        public Builder status(String v) { f.status = v; return this; }
        public Friendship build() { return f; }
    }
}
