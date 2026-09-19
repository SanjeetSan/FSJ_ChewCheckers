package com.smartnutrition.service;

import com.smartnutrition.dto.response.StudentInsightResponse;
import com.smartnutrition.dto.response.StudentInsightResponse.InsightDto;
import com.smartnutrition.entity.NutritionScore;
import com.smartnutrition.entity.Student;
import com.smartnutrition.repository.NutritionScoreRepository;
import com.smartnutrition.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class NutritionInsightService {

    private final NutritionScoreRepository nutritionScoreRepository;
    private final StudentRepository studentRepository;

    public NutritionInsightService(NutritionScoreRepository nutritionScoreRepository, StudentRepository studentRepository) {
        this.nutritionScoreRepository = nutritionScoreRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public StudentInsightResponse generateInsights(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        double boxVolume = student.getBoxVolume() != null ? student.getBoxVolume().doubleValue() : 0.0;
        int targetCal = student.getLunchCalories() != null ? student.getLunchCalories() : 630;
        int targetProt = student.getLunchProtein() != null ? student.getLunchProtein() : 16;
        int targetFiber = student.getLunchFiber() != null ? student.getLunchFiber() : 8;

        // 1. Capacity Validation (Phase 4)
        String capacityStatus;
        String capacityRecommendation;
        if (boxVolume <= 0.0) {
            capacityStatus = "Not Configured";
            capacityRecommendation = "Student lunchbox profile is not configured. Please enter lunchbox dimensions to check container capacity.";
        } else if (targetCal > boxVolume * 0.70) {
            capacityStatus = "Under-sized";
            capacityRecommendation = String.format("Child's lunchbox capacity (%.0f cm³) is too small to comfortably pack a standard nutritious lunch meeting the target of %d kcal. Consider upgrading to a larger lunchbox (e.g., ~1200 cm³) or packing calorie-dense items like seed butter spreads, cheese cubes, or avocados.", boxVolume, targetCal);
        } else if (targetCal < boxVolume * 0.35) {
            capacityStatus = "Over-sized";
            capacityRecommendation = String.format("Child's lunchbox (%.0f cm³) is significantly larger than required for the target of %d kcal. Consider using smaller nested containers or silicone dividers to prevent food from shifting and losing freshness.", boxVolume, targetCal);
        } else {
            capacityStatus = "Optimal";
            capacityRecommendation = String.format("Lunchbox volume (%.0f cm³) is optimally sized for the child's lunch target of %d kcal.", boxVolume, targetCal);
        }

        // 2. Compartment-Aware Validation (Phase 5)
        String compartmentRecommendation;
        Integer compartments = student.getBoxCompartments();
        if (compartments == null || compartments <= 0) {
            compartmentRecommendation = "No compartments configured. Use small silicone cups or dividers to organize food groups.";
        } else if (compartments == 1) {
            compartmentRecommendation = "Single Compartment: Use reusable silicone dividers or muffin cups to partition protein, carbohydrates, and vegetables, ensuring a balanced meal structure without mixing.";
        } else if (compartments == 2) {
            compartmentRecommendation = "2 Compartments: Fill the main compartment with a balanced combined dish (e.g. pasta with chicken/beans) and the secondary compartment with fresh fruits or raw cucumber/carrot slices.";
        } else if (compartments == 3) {
            compartmentRecommendation = "3 Compartments: Fill Compartment 1 with lean proteins (tofu, chicken, beans), Compartment 2 with complex carbohydrates (whole-wheat wraps, brown rice), and Compartment 3 with fresh fruits and vegetable sticks.";
        } else {
            compartmentRecommendation = String.format("%d Compartments: Perfect for a bento style lunch. Divide into: 1. Main protein, 2. Whole grains, 3. Leafy green salad/vegetables, 4. Fruit slices or healthy yogurt dip.", compartments);
        }

        // 3. Historical Nutrition Insights (Phase 7)
        List<NutritionScore> scores = nutritionScoreRepository.findTop7ByStudentIdOrderByCalculatedAtDesc(studentId);
        List<InsightDto> nutritionalInsights = new ArrayList<>();

        if (scores == null || scores.isEmpty()) {
            nutritionalInsights.add(new InsightDto(
                "No Scan Data Available",
                "Log your child's first lunchbox meal using the scan camera to generate dynamic insights.",
                "New Profile",
                "badge-violet"
            ));
        } else {
            double totalCal = 0.0;
            double totalProt = 0.0;
            double totalFiber = 0.0;
            double totalCarbs = 0.0;
            double totalScore = 0.0;
            
            for (NutritionScore score : scores) {
                totalCal += score.getTotalConsumedCalories() != null ? score.getTotalConsumedCalories().doubleValue() : 0.0;
                totalProt += score.getTotalConsumedProteinG() != null ? score.getTotalConsumedProteinG().doubleValue() : 0.0;
                totalFiber += score.getTotalConsumedFiberG() != null ? score.getTotalConsumedFiberG().doubleValue() : 0.0;
                totalCarbs += score.getTotalConsumedCarbsG() != null ? score.getTotalConsumedCarbsG().doubleValue() : 0.0;
                totalScore += score.getScore() != null ? score.getScore().doubleValue() : 0.0;
            }

            double count = scores.size();
            double avgCal = totalCal / count;
            double avgProt = totalProt / count;
            double avgFiber = totalFiber / count;
            double avgScore = totalScore / count;

            if (avgCal < targetCal * 0.70) {
                nutritionalInsights.add(new InsightDto(
                    "Low Energy Intake Trend",
                    String.format("Child is consuming only %.0f kcal on average against a lunch target of %d kcal. Consider adding nutrient-dense spreads or healthy fats like nuts or cheese.", avgCal, targetCal),
                    "Needs Attention",
                    "badge-partial"
                ));
            } else {
                nutritionalInsights.add(new InsightDto(
                    "Energy Target Sustained",
                    String.format("Child is hitting an average of %.0f kcal, which matches the target window of %d kcal. Excellent consistency!", avgCal, targetCal),
                    "Excellent",
                    "badge-full"
                ));
            }

            if (avgProt < targetProt * 0.80) {
                nutritionalInsights.add(new InsightDto(
                    "Protein Boost Required",
                    String.format("Average protein intake is %.1fg (Target: %dg). Add quick protein items such as hard-boiled eggs, beans, or cheese cubes to Compartment 1.", avgProt, targetProt),
                    "Needs Attention",
                    "badge-partial"
                ));
            }

            if (avgFiber < targetFiber * 0.80) {
                nutritionalInsights.add(new InsightDto(
                    "Fiber Deficiency Risk",
                    String.format("Fiber intake averages %.1fg against a target of %dg. Include pear slices, berries, or baby carrots to boost digestion.", avgFiber, targetFiber),
                    "Needs Attention",
                    "badge-partial"
                ));
            }

            if (avgCal > 0.0 && (totalCarbs * 4.0 / count) > (avgCal * 0.65)) {
                nutritionalInsights.add(new InsightDto(
                    "High Carbohydrate Dominance",
                    "Carbohydrates exceed 65% of the total meal calorie intake. Try swapping refined white flour wraps for whole-wheat wraps or adding raw veggie sides.",
                    "High Carbs",
                    "badge-partial"
                ));
            }

            if (scores.size() >= 3) {
                double firstScore = scores.get(scores.size() - 1).getScore().doubleValue();
                double lastScore = scores.get(0).getScore().doubleValue();
                if (lastScore < firstScore - 10.0) {
                    nutritionalInsights.add(new InsightDto(
                        "Lunch Consumption Decreasing",
                        "Lunchbox clearance scores show a downward trend over consecutive school days. Consider reviewing portion sizes or asking the child about preferences.",
                        "Monitoring",
                        "badge-partial"
                    ));
                }
            }

            if (boxVolume > 0.0 && avgCal < targetCal * 0.50) {
                nutritionalInsights.add(new InsightDto(
                    "Lunchbox Under-utilized",
                    "The portion packed is low relative to the container size. Consider switching to a smaller box to prevent food shifting and preserve texture.",
                    "Tip",
                    "badge-violet"
                ));
            }
        }

        return new StudentInsightResponse(capacityStatus, capacityRecommendation, compartmentRecommendation, nutritionalInsights);
    }
}
