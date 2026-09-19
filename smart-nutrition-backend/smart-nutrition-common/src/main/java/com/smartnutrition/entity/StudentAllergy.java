package com.smartnutrition.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_allergies")
public class StudentAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "allergen_name", nullable = false, length = 100)
    private String allergenName;

    @Column(length = 20)
    private String severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noted_by")
    private User notedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public StudentAllergy() {}

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public String getAllergenName() { return allergenName; }
    public String getSeverity() { return severity; }
    public User getNotedBy() { return notedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStudent(Student student) { this.student = student; }
    public void setAllergenName(String allergenName) { this.allergenName = allergenName; }
    public void setSeverity(String severity) { this.severity = severity; }
    public void setNotedBy(User notedBy) { this.notedBy = notedBy; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final StudentAllergy sa = new StudentAllergy();
        public Builder student(Student v) { sa.student = v; return this; }
        public Builder allergenName(String v) { sa.allergenName = v; return this; }
        public Builder severity(String v) { sa.severity = v; return this; }
        public Builder notedBy(User v) { sa.notedBy = v; return this; }
        public StudentAllergy build() { return sa; }
    }
}
