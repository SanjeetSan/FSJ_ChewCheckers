package com.smartnutrition.entity;

import com.smartnutrition.enums.NutritionClassification;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_scores")
public class NutritionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NutritionClassification classification;

    @Column(name = "total_consumed_calories", precision = 8, scale = 2)
    private BigDecimal totalConsumedCalories;

    @Column(name = "total_consumed_protein_g", precision = 8, scale = 2)
    private BigDecimal totalConsumedProteinG;

    @Column(name = "total_consumed_carbs_g", precision = 8, scale = 2)
    private BigDecimal totalConsumedCarbsG;

    @Column(name = "total_consumed_fat_g", precision = 8, scale = 2)
    private BigDecimal totalConsumedFatG;

    @Column(name = "total_consumed_fiber_g", precision = 8, scale = 2)
    private BigDecimal totalConsumedFiberG;

    @Column(name = "lunch_calorie_target", precision = 8, scale = 2)
    private BigDecimal lunchCalorieTarget;

    @Column(name = "lunch_protein_target", precision = 8, scale = 2)
    private BigDecimal lunchProteinTarget;

    @CreationTimestamp
    @Column(name = "calculated_at", updatable = false)
    private LocalDateTime calculatedAt;

    public NutritionScore() {}

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Meal getMeal() { return meal; }
    public BigDecimal getScore() { return score; }
    public NutritionClassification getClassification() { return classification; }
    public BigDecimal getTotalConsumedCalories() { return totalConsumedCalories; }
    public BigDecimal getTotalConsumedProteinG() { return totalConsumedProteinG; }
    public BigDecimal getTotalConsumedCarbsG() { return totalConsumedCarbsG; }
    public BigDecimal getTotalConsumedFatG() { return totalConsumedFatG; }
    public BigDecimal getTotalConsumedFiberG() { return totalConsumedFiberG; }
    public BigDecimal getLunchCalorieTarget() { return lunchCalorieTarget; }
    public BigDecimal getLunchProteinTarget() { return lunchProteinTarget; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final NutritionScore ns = new NutritionScore();
        public Builder student(Student v) { ns.student = v; return this; }
        public Builder meal(Meal v) { ns.meal = v; return this; }
        public Builder score(BigDecimal v) { ns.score = v; return this; }
        public Builder classification(NutritionClassification v) { ns.classification = v; return this; }
        public Builder totalConsumedCalories(BigDecimal v) { ns.totalConsumedCalories = v; return this; }
        public Builder totalConsumedProteinG(BigDecimal v) { ns.totalConsumedProteinG = v; return this; }
        public Builder totalConsumedCarbsG(BigDecimal v) { ns.totalConsumedCarbsG = v; return this; }
        public Builder totalConsumedFatG(BigDecimal v) { ns.totalConsumedFatG = v; return this; }
        public Builder totalConsumedFiberG(BigDecimal v) { ns.totalConsumedFiberG = v; return this; }
        public Builder lunchCalorieTarget(BigDecimal v) { ns.lunchCalorieTarget = v; return this; }
        public Builder lunchProteinTarget(BigDecimal v) { ns.lunchProteinTarget = v; return this; }
        public NutritionScore build() { return ns; }
    }
}
