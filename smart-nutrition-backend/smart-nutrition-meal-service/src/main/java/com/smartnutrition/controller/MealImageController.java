package com.smartnutrition.controller;

import com.smartnutrition.service.MealImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.smartnutrition.dto.request.FoodItemDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meals")
@Tag(name = "Meals Image Upload", description = "Endpoints for uploading meal images captured by camera")
public class MealImageController {

    private final MealImageService mealImageService;

    public MealImageController(MealImageService mealImageService) {
        this.mealImageService = mealImageService;
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload a meal image",
        description = "Uploads a meal image captured from a parent's or teacher's device camera. Returns the public access URL of the stored file, along with the AI-analyzed food items and their nutritional details.",
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "Image uploaded and analyzed successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"imageUrl\": \"/uploads/a1b2c3d4-e5f6.jpg\", \"extractedFoodItems\": []}"))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid file or unsupported image format")
        }
    )
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "The image file captured from camera", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Optional food keyword to force a specific mock response during development (e.g. 'burger', 'roti', 'salad')", required = false)
            @RequestParam(value = "mockFood", required = false) String mockFood) {
        try {
            String imageUrl = mealImageService.storeImage(file);
            List<FoodItemDto> extractedFoodItems = mealImageService.analyzeImage(file, mockFood);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "imageUrl", imageUrl,
                    "extractedFoodItems", extractedFoodItems
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to store image: " + e.getMessage()));
        }
    }
}
