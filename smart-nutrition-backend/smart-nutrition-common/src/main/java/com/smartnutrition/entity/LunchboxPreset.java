package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lunchbox_presets")
public class LunchboxPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "preset_name", nullable = false, length = 100)
    private String presetName;

    @Column(name = "length_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal widthCm;

    @Column(name = "height_cm", nullable = false, precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LunchboxPreset() {}

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public String getPresetName() { return presetName; }
    public BigDecimal getLengthCm() { return lengthCm; }
    public BigDecimal getWidthCm() { return widthCm; }
    public BigDecimal getHeightCm() { return heightCm; }
    public String getNotes() { return notes; }
    public Boolean getIsDefault() { return isDefault; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setStudent(Student student) { this.student = student; }
    public void setPresetName(String presetName) { this.presetName = presetName; }
    public void setLengthCm(BigDecimal lengthCm) { this.lengthCm = lengthCm; }
    public void setWidthCm(BigDecimal widthCm) { this.widthCm = widthCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final LunchboxPreset p = new LunchboxPreset();
        public Builder id(Long v) { p.id = v; return this; }
        public Builder student(Student v) { p.student = v; return this; }
        public Builder presetName(String v) { p.presetName = v; return this; }
        public Builder lengthCm(BigDecimal v) { p.lengthCm = v; return this; }
        public Builder widthCm(BigDecimal v) { p.widthCm = v; return this; }
        public Builder heightCm(BigDecimal v) { p.heightCm = v; return this; }
        public Builder notes(String v) { p.notes = v; return this; }
        public Builder isDefault(Boolean v) { p.isDefault = v; return this; }
        public LunchboxPreset build() { return p; }
    }
}
