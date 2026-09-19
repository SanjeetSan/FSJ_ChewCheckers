package com.smartnutrition.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = true)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = true)
    private Class_ studentClass;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "roll_number", length = 20)
    private String rollNumber;

    @Column(name = "student_code", unique = true, nullable = false, length = 20)
    private String studentCode;

    @Column(length = 10)
    private String gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "blood_group", length = 5)
    private String bloodGroup;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "daily_calories")
    private Integer dailyCalories;

    @Column(name = "daily_protein")
    private Integer dailyProtein;

    @Column(name = "daily_carbs")
    private Integer dailyCarbs;

    @Column(name = "daily_fat")
    private Integer dailyFat;

    @Column(name = "daily_fiber")
    private Integer dailyFiber;

    @Column(name = "lunch_calories")
    private Integer lunchCalories;

    @Column(name = "lunch_protein")
    private Integer lunchProtein;

    @Column(name = "lunch_carbs")
    private Integer lunchCarbs;

    @Column(name = "lunch_fat")
    private Integer lunchFat;

    @Column(name = "lunch_fiber")
    private Integer lunchFiber;

    @Column(name = "box_length", precision = 5, scale = 2)
    private BigDecimal boxLength;

    @Column(name = "box_width", precision = 5, scale = 2)
    private BigDecimal boxWidth;

    @Column(name = "box_depth", precision = 5, scale = 2)
    private BigDecimal boxDepth;

    @Column(name = "box_volume", precision = 8, scale = 2)
    private BigDecimal boxVolume;

    public Student() {}

    public Long getId() { return id; }
    public School getSchool() { return school; }
    public Class_ getStudentClass() { return studentClass; }
    public String getName() { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getStudentCode() { return studentCode; }
    public String getGender() { return gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public BigDecimal getWeightKg() { return weightKg; }
    public BigDecimal getHeightCm() { return heightCm; }
    public String getBloodGroup() { return bloodGroup; }
    public Boolean getIsActive() { return isActive; }

    public Integer getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(Integer v) { this.dailyCalories = v; }
    public Integer getDailyProtein() { return dailyProtein; }
    public void setDailyProtein(Integer v) { this.dailyProtein = v; }
    public Integer getDailyCarbs() { return dailyCarbs; }
    public void setDailyCarbs(Integer v) { this.dailyCarbs = v; }
    public Integer getDailyFat() { return dailyFat; }
    public void setDailyFat(Integer v) { this.dailyFat = v; }
    public Integer getDailyFiber() { return dailyFiber; }
    public void setDailyFiber(Integer v) { this.dailyFiber = v; }

    public Integer getLunchCalories() { return lunchCalories; }
    public void setLunchCalories(Integer v) { this.lunchCalories = v; }
    public Integer getLunchProtein() { return lunchProtein; }
    public void setLunchProtein(Integer v) { this.lunchProtein = v; }
    public Integer getLunchCarbs() { return lunchCarbs; }
    public void setLunchCarbs(Integer v) { this.lunchCarbs = v; }
    public Integer getLunchFat() { return lunchFat; }
    public void setLunchFat(Integer v) { this.lunchFat = v; }
    public Integer getLunchFiber() { return lunchFiber; }
    public void setLunchFiber(Integer v) { this.lunchFiber = v; }

    public BigDecimal getBoxLength() { return boxLength; }
    public void setBoxLength(BigDecimal v) { this.boxLength = v; }
    public BigDecimal getBoxWidth() { return boxWidth; }
    public void setBoxWidth(BigDecimal v) { this.boxWidth = v; }
    public BigDecimal getBoxDepth() { return boxDepth; }
    public void setBoxDepth(BigDecimal v) { this.boxDepth = v; }
    public BigDecimal getBoxVolume() { return boxVolume; }
    public void setBoxVolume(BigDecimal v) { this.boxVolume = v; }

    @Column(name = "box_shape", length = 50)
    private String boxShape;
    @Column(name = "box_compartments")
    private Integer boxCompartments;

    public String getBoxShape() { return boxShape; }
    public void setBoxShape(String v) { this.boxShape = v; }
    public Integer getBoxCompartments() { return boxCompartments; }
    public void setBoxCompartments(Integer v) { this.boxCompartments = v; }

    public void setSchool(School school) { this.school = school; }
    public void setStudentClass(Class_ studentClass) { this.studentClass = studentClass; }
    public void setName(String name) { this.name = name; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public void setGender(String gender) { this.gender = gender; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Student s = new Student();
        public Builder school(School v) { s.school = v; return this; }
        public Builder studentClass(Class_ v) { s.studentClass = v; return this; }
        public Builder name(String v) { s.name = v; return this; }
        public Builder rollNumber(String v) { s.rollNumber = v; return this; }
        public Builder studentCode(String v) { s.studentCode = v; return this; }
        public Builder gender(String v) { s.gender = v; return this; }
        public Builder dateOfBirth(LocalDate v) { s.dateOfBirth = v; return this; }
        public Builder weightKg(BigDecimal v) { s.weightKg = v; return this; }
        public Builder heightCm(BigDecimal v) { s.heightCm = v; return this; }
        public Builder bloodGroup(String v) { s.bloodGroup = v; return this; }
        public Builder isActive(Boolean v) { s.isActive = v; return this; }
        public Builder boxShape(String v) { s.boxShape = v; return this; }
        public Builder boxCompartments(Integer v) { s.boxCompartments = v; return this; }
        public Student build() { return s; }
    }
}
