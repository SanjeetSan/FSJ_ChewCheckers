package com.smartnutrition.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "classes")
public class Class_ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(length = 10)
    private String section;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "class_code", unique = true, nullable = false, length = 20)
    private String classCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public Class_() {}

    public Long getId() { return id; }
    public School getSchool() { return school; }
    public String getClassName() { return className; }
    public String getSection() { return section; }
    public String getAcademicYear() { return academicYear; }
    public String getClassCode() { return classCode; }
    public User getTeacher() { return teacher; }
    public Boolean getIsActive() { return isActive; }

    public void setSchool(School school) { this.school = school; }
    public void setClassName(String className) { this.className = className; }
    public void setSection(String section) { this.section = section; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public void setClassCode(String classCode) { this.classCode = classCode; }
    public void setTeacher(User teacher) { this.teacher = teacher; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Class_ c = new Class_();
        public Builder school(School v) { c.school = v; return this; }
        public Builder className(String v) { c.className = v; return this; }
        public Builder section(String v) { c.section = v; return this; }
        public Builder academicYear(String v) { c.academicYear = v; return this; }
        public Builder classCode(String v) { c.classCode = v; return this; }
        public Builder teacher(User v) { c.teacher = v; return this; }
        public Builder isActive(Boolean v) { c.isActive = v; return this; }
        public Class_ build() { return c; }
    }
}
