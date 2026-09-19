package com.smartnutrition.entity;

import com.smartnutrition.enums.Relationship;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "parent_student",
    uniqueConstraints = @UniqueConstraint(columnNames = {"parent_id", "student_id"}))
public class ParentStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Relationship relationship;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public ParentStudent() {}

    public Long getId() { return id; }
    public User getParent() { return parent; }
    public Student getStudent() { return student; }
    public Relationship getRelationship() { return relationship; }
    public Boolean getIsPrimary() { return isPrimary; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setParent(User parent) { this.parent = parent; }
    public void setStudent(Student student) { this.student = student; }
    public void setRelationship(Relationship relationship) { this.relationship = relationship; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final ParentStudent ps = new ParentStudent();
        public Builder parent(User v) { ps.parent = v; return this; }
        public Builder student(Student v) { ps.student = v; return this; }
        public Builder relationship(Relationship v) { ps.relationship = v; return this; }
        public Builder isPrimary(Boolean v) { ps.isPrimary = v; return this; }
        public ParentStudent build() { return ps; }
    }
}
