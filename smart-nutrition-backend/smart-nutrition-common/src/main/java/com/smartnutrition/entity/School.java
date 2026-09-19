package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "schools")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public School() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAcademicYear() { return academicYear; }
    public String getAddress() { return address; }
    public Boolean getIsActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public void setAddress(String address) { this.address = address; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final School s = new School();
        public Builder name(String v) { s.name = v; return this; }
        public Builder academicYear(String v) { s.academicYear = v; return this; }
        public Builder address(String v) { s.address = v; return this; }
        public Builder isActive(Boolean v) { s.isActive = v; return this; }
        public School build() { return s; }
    }
}
