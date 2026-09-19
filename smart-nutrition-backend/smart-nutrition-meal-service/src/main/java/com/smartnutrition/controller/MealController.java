package com.smartnutrition.controller;

import com.smartnutrition.dto.request.PostMealUploadRequest;
import com.smartnutrition.dto.request.PreMealUploadRequest;
import com.smartnutrition.dto.response.MealResponse;
import com.smartnutrition.dto.response.NutritionScoreResponse;
import com.smartnutrition.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meals", description = "Pre-meal and post-meal upload endpoints for parents and teachers")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping("/pre-meal")
    @Operation(summary = "Parent uploads pre-meal image and manually enters food item details")
    public ResponseEntity<MealResponse> uploadPreMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PreMealUploadRequest request) {
        MealResponse response = mealService.processPreMealUpload(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/post-meal")
    @Operation(summary = "Teacher uploads post-meal image, enters consumption %, triggers 35% Lunch RDA scoring")
    public ResponseEntity<NutritionScoreResponse> uploadPostMeal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostMealUploadRequest request) {
        NutritionScoreResponse response = mealService.processPostMealUpload(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/consumption-quick")
    @Operation(summary = "Teacher 1-click quick action consumption percentage record")
    public ResponseEntity<MealResponse> recordQuickConsumption(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody com.smartnutrition.dto.request.QuickConsumptionRequest request) {
        MealResponse response = mealService.processQuickConsumption(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{mealId}/leftover-image")
    @Operation(summary = "Teacher uploads leftover after-meal photo for Gemini AI leftover analysis")
    public ResponseEntity<MealResponse> uploadLeftoverImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long mealId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        MealResponse response = mealService.processLeftoverImageUpload(userDetails.getUsername(), mealId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today/class/{classCode}")
    @Operation(summary = "Get today's meal consumption status roster for a class")
    public ResponseEntity<java.util.List<MealResponse>> getTodayClassMeals(@PathVariable String classCode) {
        return ResponseEntity.ok(mealService.getTodayMealsForClass(classCode));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Parent or teacher gets meal history for a student")
    public ResponseEntity<java.util.List<com.smartnutrition.dto.response.MealResponse>> getStudentMeals(@PathVariable Long studentId) {
        return ResponseEntity.ok(mealService.getMealsForStudent(studentId));
    }
}

