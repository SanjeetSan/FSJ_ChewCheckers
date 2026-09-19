package com.smartnutrition.entity;

import com.smartnutrition.enums.FoodItemSource;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "meal_food_items")
public class MealFoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @Column(name = "food_name", nullable = false, length = 150)
    private String foodName;

    @Column(length = 50)
    private String quantity;

    @Column(name = "cooking_note", length = 255)
    private String cookingNote;

    @Column(precision = 8, scale = 2)
    private BigDecimal calories;

    @Column(name = "protein_g", precision = 8, scale = 2)
    private BigDecimal proteinG;

    @Column(name = "carbs_g", precision = 8, scale = 2)
    private BigDecimal carbsG;

    @Column(name = "fat_g", precision = 8, scale = 2)
    private BigDecimal fatG;

    @Column(name = "fiber_g", precision = 8, scale = 2)
    private BigDecimal fiberG;

    @Column(name = "consumption_percentage", precision = 5, scale = 2)
    private BigDecimal consumptionPercentage;

    @Column(name = "consumed_calories", precision = 8, scale = 2)
    private BigDecimal consumedCalories;

    @Column(name = "consumed_protein_g", precision = 8, scale = 2)
    private BigDecimal consumedProteinG;

    @Column(name = "consumed_carbs_g", precision = 8, scale = 2)
    private BigDecimal consumedCarbsG;

    @Column(name = "consumed_fat_g", precision = 8, scale = 2)
    private BigDecimal consumedFatG;

    @Column(name = "consumed_fiber_g", precision = 8, scale = 2)
    private BigDecimal consumedFiberG;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FoodItemSource source;

    public MealFoodItem() {}

    public Long getId() { return id; }
    public Meal getMeal() { return meal; }
    public String getFoodName() { return foodName; }
    public String getQuantity() { return quantity; }
    public String getCookingNote() { return cookingNote; }
    public BigDecimal getCalories() { return calories; }
    public BigDecimal getProteinG() { return proteinG; }
    public BigDecimal getCarbsG() { return carbsG; }
    public BigDecimal getFatG() { return fatG; }
    public BigDecimal getFiberG() { return fiberG; }
    public BigDecimal getConsumptionPercentage() { return consumptionPercentage; }
    public BigDecimal getConsumedCalories() { return consumedCalories; }
    public BigDecimal getConsumedProteinG() { return consumedProteinG; }
    public BigDecimal getConsumedCarbsG() { return consumedCarbsG; }
    public BigDecimal getConsumedFatG() { return consumedFatG; }
    public BigDecimal getConsumedFiberG() { return consumedFiberG; }
    public FoodItemSource getSource() { return source; }

    public void setMeal(Meal meal) { this.meal = meal; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public void setCookingNote(String cookingNote) { this.cookingNote = cookingNote; }
    public void setCalories(BigDecimal calories) { this.calories = calories; }
    public void setProteinG(BigDecimal proteinG) { this.proteinG = proteinG; }
    public void setCarbsG(BigDecimal carbsG) { this.carbsG = carbsG; }
    public void setFatG(BigDecimal fatG) { this.fatG = fatG; }
    public void setFiberG(BigDecimal fiberG) { this.fiberG = fiberG; }
    public void setConsumptionPercentage(BigDecimal consumptionPercentage) { this.consumptionPercentage = consumptionPercentage; }
    public void setConsumedCalories(BigDecimal consumedCalories) { this.consumedCalories = consumedCalories; }
    public void setConsumedProteinG(BigDecimal consumedProteinG) { this.consumedProteinG = consumedProteinG; }
    public void setConsumedCarbsG(BigDecimal consumedCarbsG) { this.consumedCarbsG = consumedCarbsG; }
    public void setConsumedFatG(BigDecimal consumedFatG) { this.consumedFatG = consumedFatG; }
    public void setConsumedFiberG(BigDecimal consumedFiberG) { this.consumedFiberG = consumedFiberG; }
    public void setSource(FoodItemSource source) { this.source = source; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MealFoodItem i = new MealFoodItem();
        public Builder meal(Meal v) { i.meal = v; return this; }
        public Builder foodName(String v) { i.foodName = v; return this; }
        public Builder quantity(String v) { i.quantity = v; return this; }
        public Builder cookingNote(String v) { i.cookingNote = v; return this; }
        public Builder calories(BigDecimal v) { i.calories = v; return this; }
        public Builder proteinG(BigDecimal v) { i.proteinG = v; return this; }
        public Builder carbsG(BigDecimal v) { i.carbsG = v; return this; }
        public Builder fatG(BigDecimal v) { i.fatG = v; return this; }
        public Builder fiberG(BigDecimal v) { i.fiberG = v; return this; }
        public Builder source(FoodItemSource v) { i.source = v; return this; }
        public MealFoodItem build() { return i; }
    }
}
