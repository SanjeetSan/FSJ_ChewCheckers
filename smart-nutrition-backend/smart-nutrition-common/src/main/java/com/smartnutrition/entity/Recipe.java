package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ingredients;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Recipe() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getIngredients() { return ingredients; }
    public String getInstructions() { return instructions; }
    public User getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Recipe r = new Recipe();
        public Builder title(String v) { r.title = v; return this; }
        public Builder description(String v) { r.description = v; return this; }
        public Builder ingredients(String v) { r.ingredients = v; return this; }
        public Builder instructions(String v) { r.instructions = v; return this; }
        public Builder createdBy(User v) { r.createdBy = v; return this; }
        public Recipe build() { return r; }
    }
}
