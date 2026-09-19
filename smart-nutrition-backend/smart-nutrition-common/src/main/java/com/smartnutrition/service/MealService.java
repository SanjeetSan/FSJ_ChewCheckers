package com.smartnutrition.service;

import com.smartnutrition.dto.request.FoodItemDto;
import com.smartnutrition.dto.request.PostMealUploadRequest;
import com.smartnutrition.dto.request.PreMealUploadRequest;
import com.smartnutrition.dto.response.MealResponse;
import com.smartnutrition.dto.response.NutritionScoreResponse;
import com.smartnutrition.entity.Meal;
import com.smartnutrition.entity.MealFoodItem;
import com.smartnutrition.entity.NutritionScore;
import com.smartnutrition.entity.Student;
import com.smartnutrition.entity.User;
import com.smartnutrition.enums.FoodItemSource;
import com.smartnutrition.enums.MealStatus;
import com.smartnutrition.repository.MealFoodItemRepository;
import com.smartnutrition.repository.MealRepository;
import com.smartnutrition.repository.StudentRepository;
import com.smartnutrition.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MealService {

    private final MealRepository mealRepository;
    private final MealFoodItemRepository mealFoodItemRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final NutritionScoringService nutritionScoringService;
    private final MealImageService mealImageService;

    public MealService(MealRepository mealRepository,
                       MealFoodItemRepository mealFoodItemRepository,
                       StudentRepository studentRepository,
                       UserRepository userRepository,
                       NutritionScoringService nutritionScoringService,
                       MealImageService mealImageService) {
        this.mealRepository = mealRepository;
        this.mealFoodItemRepository = mealFoodItemRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.nutritionScoringService = nutritionScoringService;
        this.mealImageService = mealImageService;
    }

    @Transactional
    public MealResponse processPreMealUpload(String parentEmail, PreMealUploadRequest request) {
        User foundParent = (parentEmail != null) ? userRepository.findByEmail(parentEmail).orElse(null) : null;
        if (foundParent == null) {
            foundParent = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.PARENT)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }
        final User parent = foundParent;

        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        LocalDate today = LocalDate.now();

        Meal meal = mealRepository.findByStudentIdAndMealDate(student.getId(), today)
                .orElseGet(() -> Meal.builder()
                        .student(student)
                        .mealDate(today)
                        .uploadedByParent(parent)
                        .status(MealStatus.PRE_MEAL_UPLOADED)
                        .build());

        meal.setPreMealImageUrl(request.preMealImageUrl());
        meal.setBoxLength(request.boxLength());
        meal.setBoxWidth(request.boxWidth());
        meal.setBoxHeight(request.boxHeight());
        meal.setLunchboxPresetId(request.lunchboxPresetId());
        meal.setLunchboxPresetName(request.lunchboxPresetName());
        meal.setStatus(MealStatus.PRE_MEAL_UPLOADED);

        Meal savedMeal = mealRepository.save(meal);

        List<MealFoodItem> existing = mealFoodItemRepository.findByMealId(savedMeal.getId());
        mealFoodItemRepository.deleteAll(existing);

        List<MealFoodItem> foodItemEntities = new ArrayList<>();

        for (FoodItemDto itemDto : request.foodItems()) {
            MealFoodItem item = MealFoodItem.builder()
                    .meal(savedMeal)
                    .foodName(itemDto.foodName())
                    .quantity(itemDto.quantity() != null ? itemDto.quantity() : "1 serving")
                    .cookingNote(itemDto.cookingNote())
                    .calories(itemDto.calories() != null ? itemDto.calories() : new BigDecimal("250.00"))
                    .proteinG(itemDto.proteinG() != null ? itemDto.proteinG() : new BigDecimal("8.00"))
                    .carbsG(itemDto.carbsG() != null ? itemDto.carbsG() : new BigDecimal("35.00"))
                    .fatG(itemDto.fatG() != null ? itemDto.fatG() : new BigDecimal("6.00"))
                    .fiberG(itemDto.fiberG() != null ? itemDto.fiberG() : new BigDecimal("4.00"))
                    .source(itemDto.source() != null ? itemDto.source() : FoodItemSource.PARENT_EDITED)
                    .build();
            foodItemEntities.add(item);
        }

        List<MealFoodItem> savedItems = mealFoodItemRepository.saveAll(foodItemEntities);
        return mapToMealResponse(savedMeal, savedItems);
    }

    @Transactional
    public MealResponse processQuickConsumption(String teacherEmail, com.smartnutrition.dto.request.QuickConsumptionRequest request) {
        User teacher = (teacherEmail != null) ? userRepository.findByEmail(teacherEmail).orElse(null) : null;
        if (teacher == null) {
            teacher = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.TEACHER)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Meal meal = mealRepository.findById(request.mealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found with ID: " + request.mealId()));

        meal.setUploadedByTeacher(teacher);
        BigDecimal pct = request.overallConsumptionPercentage() != null ? request.overallConsumptionPercentage() : new BigDecimal("100");

        if (pct.compareTo(new BigDecimal("100")) >= 0) {
            meal.setStatus(MealStatus.FULLY_CONSUMED);
            List<MealFoodItem> items = mealFoodItemRepository.findByMealId(meal.getId());
            for (MealFoodItem item : items) {
                item.setConsumptionPercentage(new BigDecimal("100.00"));
                item.setConsumedCalories(item.getCalories() != null ? item.getCalories() : BigDecimal.ZERO);
                item.setConsumedProteinG(item.getProteinG() != null ? item.getProteinG() : BigDecimal.ZERO);
                item.setConsumedCarbsG(item.getCarbsG() != null ? item.getCarbsG() : BigDecimal.ZERO);
                item.setConsumedFatG(item.getFatG() != null ? item.getFatG() : BigDecimal.ZERO);
                item.setConsumedFiberG(item.getFiberG() != null ? item.getFiberG() : BigDecimal.ZERO);
            }
            mealFoodItemRepository.saveAll(items);
            mealRepository.save(meal);
            nutritionScoringService.calculateAndSaveScore(meal.getStudent(), meal, items);
            return mapToMealResponse(meal, items);
        } else {
            meal.setStatus(MealStatus.PENDING_LEFTOVER_ANALYSIS);
            List<MealFoodItem> items = mealFoodItemRepository.findByMealId(meal.getId());
            BigDecimal ratio = pct.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            for (MealFoodItem item : items) {
                item.setConsumptionPercentage(pct.setScale(2, RoundingMode.HALF_UP));
                item.setConsumedCalories(item.getCalories() != null ? item.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                item.setConsumedProteinG(item.getProteinG() != null ? item.getProteinG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                item.setConsumedCarbsG(item.getCarbsG() != null ? item.getCarbsG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                item.setConsumedFatG(item.getFatG() != null ? item.getFatG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                item.setConsumedFiberG(item.getFiberG() != null ? item.getFiberG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            }
            mealFoodItemRepository.saveAll(items);
            mealRepository.save(meal);
            return mapToMealResponse(meal, items);
        }
    }

    @Transactional
    public MealResponse processLeftoverImageUpload(String teacherEmail, Long mealId, org.springframework.web.multipart.MultipartFile file) {
        User teacher = (teacherEmail != null) ? userRepository.findByEmail(teacherEmail).orElse(null) : null;
        if (teacher == null) {
            teacher = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == com.smartnutrition.enums.Role.TEACHER)
                    .findFirst()
                    .orElseGet(() -> userRepository.findAll().get(0));
        }

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Meal not found with ID: " + mealId));

        try {
            String imagePath = mealImageService.storeImage(file);
            meal.setPostMealImageUrl(imagePath);
        } catch (Exception e) {
            System.err.println("Could not store leftover image: " + e.getMessage());
        }

        meal.setUploadedByTeacher(teacher);
        meal.setStatus(MealStatus.PARTIALLY_CONSUMED);

        List<MealFoodItem> items = mealFoodItemRepository.findByMealId(meal.getId());
        java.util.Map<String, BigDecimal> leftoverAnalysis = mealImageService.analyzeLeftovers(file, items);

        for (MealFoodItem item : items) {
            String key = item.getFoodName() != null ? item.getFoodName().toLowerCase().trim() : "";
            BigDecimal pct = leftoverAnalysis.getOrDefault(key, new BigDecimal("50.00"));
            BigDecimal ratio = pct.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

            item.setConsumptionPercentage(pct.setScale(2, RoundingMode.HALF_UP));
            item.setConsumedCalories(item.getCalories() != null ? item.getCalories().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedProteinG(item.getProteinG() != null ? item.getProteinG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedCarbsG(item.getCarbsG() != null ? item.getCarbsG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedFatG(item.getFatG() != null ? item.getFatG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedFiberG(item.getFiberG() != null ? item.getFiberG().multiply(ratio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        }

        mealFoodItemRepository.saveAll(items);
        Meal savedMeal = mealRepository.save(meal);
        nutritionScoringService.calculateAndSaveScore(savedMeal.getStudent(), savedMeal, items);

        return mapToMealResponse(savedMeal, items);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getTodayMealsForClass(String classCode) {
        LocalDate today = LocalDate.now();
        List<Student> students = studentRepository.findByClassCode(classCode);
        List<MealResponse> result = new ArrayList<>();

        for (Student student : students) {
            Meal meal = mealRepository.findByStudentIdAndMealDate(student.getId(), today).orElse(null);
            if (meal != null) {
                List<MealFoodItem> items = mealFoodItemRepository.findByMealId(meal.getId());
                result.add(mapToMealResponse(meal, items));
            } else {
                result.add(new MealResponse(
                        null,
                        student.getId(),
                        today,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        MealStatus.MEAL_NOT_PACKED,
                        List.of()
                ));
            }
        }
        return result;
    }

    @Transactional
    public NutritionScoreResponse processPostMealUpload(String teacherEmail, PostMealUploadRequest request) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Teacher user not found"));

        Meal meal = mealRepository.findById(request.mealId())
                .orElseThrow(() -> new IllegalArgumentException("Meal not found with ID: " + request.mealId()));

        meal.setPostMealImageUrl(request.postMealImageUrl());
        meal.setStatus(MealStatus.POST_MEAL_UPLOADED);
        meal.setUploadedByTeacher(teacher);
        mealRepository.save(meal);

        List<MealFoodItem> items = mealFoodItemRepository.findByMealId(meal.getId());

        java.util.Map<String, BigDecimal> consumptionMap = new java.util.HashMap<>();
        if (request.foodItemConsumptions() != null) {
            for (com.smartnutrition.dto.request.FoodItemConsumptionDto fc : request.foodItemConsumptions()) {
                if (fc.foodName() != null) {
                    consumptionMap.put(fc.foodName().toLowerCase().trim(), fc.consumedPercentage());
                }
            }
        }

        BigDecimal fallbackRatio = (request.overallConsumptionPercentage() != null)
                ? request.overallConsumptionPercentage().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                : new BigDecimal("1.00");

        for (MealFoodItem item : items) {
            BigDecimal percentage = item.getFoodName() != null ? consumptionMap.get(item.getFoodName().toLowerCase().trim()) : null;
            BigDecimal consumptionRatio;
            if (percentage != null) {
                consumptionRatio = percentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            } else {
                consumptionRatio = fallbackRatio;
            }

            item.setConsumptionPercentage(consumptionRatio.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
            item.setConsumedCalories(item.getCalories() != null ? item.getCalories().multiply(consumptionRatio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedProteinG(item.getProteinG() != null ? item.getProteinG().multiply(consumptionRatio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedCarbsG(item.getCarbsG() != null ? item.getCarbsG().multiply(consumptionRatio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedFatG(item.getFatG() != null ? item.getFatG().multiply(consumptionRatio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            item.setConsumedFiberG(item.getFiberG() != null ? item.getFiberG().multiply(consumptionRatio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        }
        mealFoodItemRepository.saveAll(items);

        NutritionScore score = nutritionScoringService.calculateAndSaveScore(meal.getStudent(), meal, items);
        return mapToScoreResponse(score);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getMealsForStudent(Long studentId) {
        List<Meal> meals = mealRepository.findByStudentIdOrderByMealDateDesc(studentId);
        return meals.stream().map(m -> {
            List<MealFoodItem> items = mealFoodItemRepository.findByMealId(m.getId());
            return mapToMealResponse(m, items);
        }).toList();
    }

    private NutritionScoreResponse mapToScoreResponse(NutritionScore score) {
        return new NutritionScoreResponse(
                score.getId(),
                score.getStudent().getId(),
                score.getMeal().getId(),
                score.getScore(),
                score.getClassification(),
                score.getTotalConsumedCalories(),
                score.getTotalConsumedProteinG(),
                score.getTotalConsumedCarbsG(),
                score.getTotalConsumedFatG(),
                score.getTotalConsumedFiberG(),
                score.getLunchCalorieTarget(),
                score.getLunchProteinTarget(),
                score.getCalculatedAt() != null ? score.getCalculatedAt().toString() : null
        );
    }

    private MealResponse mapToMealResponse(Meal meal, List<MealFoodItem> items) {
        List<FoodItemDto> itemDtos = items.stream().map(i -> new FoodItemDto(
                i.getFoodName(),
                i.getQuantity(),
                i.getCookingNote(),
                i.getCalories(),
                i.getProteinG(),
                i.getCarbsG(),
                i.getFatG(),
                i.getFiberG(),
                i.getSource(),
                i.getConsumptionPercentage(),
                i.getConsumedCalories(),
                i.getConsumedProteinG(),
                i.getConsumedCarbsG(),
                i.getConsumedFatG(),
                i.getConsumedFiberG()
        )).toList();

        return new MealResponse(
                meal.getId(),
                meal.getStudent().getId(),
                meal.getMealDate(),
                meal.getPreMealImageUrl(),
                meal.getPostMealImageUrl(),
                meal.getBoxLength(),
                meal.getBoxWidth(),
                meal.getBoxHeight(),
                meal.getLunchboxPresetId(),
                meal.getLunchboxPresetName(),
                meal.getStatus(),
                itemDtos
        );
    }
}

