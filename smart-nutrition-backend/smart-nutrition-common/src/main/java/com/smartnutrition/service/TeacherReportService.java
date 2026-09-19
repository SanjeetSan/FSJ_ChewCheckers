package com.smartnutrition.service;

import com.smartnutrition.dto.response.ClassReportResponse;
import com.smartnutrition.entity.Meal;
import com.smartnutrition.entity.MealFoodItem;
import com.smartnutrition.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TeacherReportService {

    private final MealRepository mealRepository;

    public TeacherReportService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public ClassReportResponse generateWeeklyReport(String classCode, String startDateStr) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(startDateStr, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            parsedDate = LocalDate.now().minusDays(7);
        }

        final LocalDate startDate = parsedDate;
        final LocalDate endDate = startDate.plusDays(7);

        List<Meal> meals = mealRepository.findAll().stream()
                .filter(m -> m.getMealDate() != null && !m.getMealDate().isBefore(startDate) && !m.getMealDate().isAfter(endDate))
                .filter(m -> m.getStudent() != null && m.getStudent().getStudentClass() != null && m.getStudent().getStudentClass().getClassCode().equalsIgnoreCase(classCode))
                .toList();

        return computeAggregateReport("WEEKLY", classCode, startDate + " to " + endDate, meals);
    }

    public ClassReportResponse generateMonthlyReport(String classCode, Integer year, Integer month) {
        int yearVal = (year != null && year > 2020) ? year : LocalDate.now().getYear();
        int monthVal = (month != null && month >= 1 && month <= 12) ? month : LocalDate.now().getMonthValue();

        YearMonth yearMonth = YearMonth.of(yearVal, monthVal);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Meal> meals = mealRepository.findAll().stream()
                .filter(m -> m.getMealDate() != null && !m.getMealDate().isBefore(startDate) && !m.getMealDate().isAfter(endDate))
                .filter(m -> m.getStudent() != null && m.getStudent().getStudentClass() != null && m.getStudent().getStudentClass().getClassCode().equalsIgnoreCase(classCode))
                .toList();

        return computeAggregateReport("MONTHLY", classCode, yearMonth.toString(), meals);
    }

    private ClassReportResponse computeAggregateReport(String reportType, String classCode, String timePeriod, List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            return new ClassReportResponse(
                    reportType,
                    classCode,
                    timePeriod,
                    0,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0.0,
                    List.of(),
                    List.of("Data will appear after meal logging begins.")
            );
        }

        long count = meals.size();
        BigDecimal totalCal = BigDecimal.ZERO;
        BigDecimal totalProt = BigDecimal.ZERO;
        BigDecimal totalCarb = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalFib = BigDecimal.ZERO;
        double totalWastePct = 0.0;

        java.util.Map<String, Integer> itemFrequency = new java.util.HashMap<>();

        for (Meal meal : meals) {
            if (meal.getFoodItems() != null && !meal.getFoodItems().isEmpty()) {
                for (MealFoodItem item : meal.getFoodItems()) {
                    BigDecimal cal = item.getConsumedCalories() != null ? item.getConsumedCalories() : item.getCalories();
                    BigDecimal prot = item.getConsumedProteinG() != null ? item.getConsumedProteinG() : item.getProteinG();
                    BigDecimal carb = item.getConsumedCarbsG() != null ? item.getConsumedCarbsG() : item.getCarbsG();
                    BigDecimal fat = item.getConsumedFatG() != null ? item.getConsumedFatG() : item.getFatG();
                    BigDecimal fib = item.getConsumedFiberG() != null ? item.getConsumedFiberG() : item.getFiberG();

                    if (cal != null) totalCal = totalCal.add(cal);
                    if (prot != null) totalProt = totalProt.add(prot);
                    if (carb != null) totalCarb = totalCarb.add(carb);
                    if (fat != null) totalFat = totalFat.add(fat);
                    if (fib != null) totalFib = totalFib.add(fib);

                    if (item.getConsumptionPercentage() != null) {
                        totalWastePct += (100.0 - item.getConsumptionPercentage().doubleValue());
                    }

                    if (item.getFoodName() != null) {
                        String name = item.getFoodName().trim();
                        itemFrequency.put(name, itemFrequency.getOrDefault(name, 0) + 1);
                    }
                }
            }
        }

        BigDecimal avgCal = totalCal.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal avgProt = totalProt.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal avgCarb = totalCarb.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal avgFat = totalFat.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        BigDecimal avgFib = totalFib.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        double avgWaste = Math.round((totalWastePct / count) * 100.0) / 100.0;

        List<String> topItems = itemFrequency.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(4)
                .map(java.util.Map.Entry::getKey)
                .toList();
        if (topItems.isEmpty()) {
            topItems = List.of("Healthy Packed Lunch");
        }

        List<String> attentionNotes;
        if (avgWaste > 25.0) {
            attentionNotes = List.of("Class leftover rate is high (" + avgWaste + "%). Consider reviewing portion sizes or asking parents about food preferences.");
        } else {
            attentionNotes = List.of("Nutritional compliance is healthy across the class.");
        }

        return new ClassReportResponse(
                reportType,
                classCode,
                timePeriod,
                count,
                avgCal,
                avgProt,
                avgCarb,
                avgFat,
                avgFib,
                avgWaste,
                topItems,
                attentionNotes
        );
    }
}
