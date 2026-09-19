package com.smartnutrition.entity;

import com.smartnutrition.enums.MealStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "meal_date"}))
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Column(name = "pre_meal_image_url", length = 500)
    private String preMealImageUrl;

    @Column(name = "post_meal_image_url", length = 500)
    private String postMealImageUrl;

    @Column(name = "box_length", precision = 5, scale = 2)
    private java.math.BigDecimal boxLength;

    @Column(name = "box_width", precision = 5, scale = 2)
    private java.math.BigDecimal boxWidth;

    @Column(name = "box_height", precision = 5, scale = 2)
    private java.math.BigDecimal boxHeight;

    @Column(name = "lunchbox_preset_id")
    private Long lunchboxPresetId;

    @Column(name = "lunchbox_preset_name", length = 100)
    private String lunchboxPresetName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MealStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_parent")
    private User uploadedByParent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_teacher")
    private User uploadedByTeacher;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealFoodItem> foodItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Meal() {}

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public LocalDate getMealDate() { return mealDate; }
    public String getPreMealImageUrl() { return preMealImageUrl; }
    public String getPostMealImageUrl() { return postMealImageUrl; }
    public java.math.BigDecimal getBoxLength() { return boxLength; }
    public java.math.BigDecimal getBoxWidth() { return boxWidth; }
    public java.math.BigDecimal getBoxHeight() { return boxHeight; }
    public Long getLunchboxPresetId() { return lunchboxPresetId; }
    public String getLunchboxPresetName() { return lunchboxPresetName; }
    public MealStatus getStatus() { return status; }
    public User getUploadedByParent() { return uploadedByParent; }
    public User getUploadedByTeacher() { return uploadedByTeacher; }
    public List<MealFoodItem> getFoodItems() { return foodItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStudent(Student student) { this.student = student; }
    public void setMealDate(LocalDate mealDate) { this.mealDate = mealDate; }
    public void setPreMealImageUrl(String preMealImageUrl) { this.preMealImageUrl = preMealImageUrl; }
    public void setPostMealImageUrl(String postMealImageUrl) { this.postMealImageUrl = postMealImageUrl; }
    public void setBoxLength(java.math.BigDecimal boxLength) { this.boxLength = boxLength; }
    public void setBoxWidth(java.math.BigDecimal boxWidth) { this.boxWidth = boxWidth; }
    public void setBoxHeight(java.math.BigDecimal boxHeight) { this.boxHeight = boxHeight; }
    public void setLunchboxPresetId(Long lunchboxPresetId) { this.lunchboxPresetId = lunchboxPresetId; }
    public void setLunchboxPresetName(String lunchboxPresetName) { this.lunchboxPresetName = lunchboxPresetName; }
    public void setStatus(MealStatus status) { this.status = status; }
    public void setUploadedByParent(User uploadedByParent) { this.uploadedByParent = uploadedByParent; }
    public void setUploadedByTeacher(User uploadedByTeacher) { this.uploadedByTeacher = uploadedByTeacher; }
    public void setFoodItems(List<MealFoodItem> foodItems) { this.foodItems = foodItems; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Meal m = new Meal();
        public Builder student(Student v) { m.student = v; return this; }
        public Builder mealDate(LocalDate v) { m.mealDate = v; return this; }
        public Builder preMealImageUrl(String v) { m.preMealImageUrl = v; return this; }
        public Builder status(MealStatus v) { m.status = v; return this; }
        public Builder uploadedByParent(User v) { m.uploadedByParent = v; return this; }
        public Builder uploadedByTeacher(User v) { m.uploadedByTeacher = v; return this; }
        public Builder boxLength(java.math.BigDecimal v) { m.boxLength = v; return this; }
        public Builder boxWidth(java.math.BigDecimal v) { m.boxWidth = v; return this; }
        public Builder boxHeight(java.math.BigDecimal v) { m.boxHeight = v; return this; }
        public Builder lunchboxPresetId(Long v) { m.lunchboxPresetId = v; return this; }
        public Builder lunchboxPresetName(String v) { m.lunchboxPresetName = v; return this; }
        public Meal build() { return m; }
    }
}
