package com.smartnutrition.controller;

import com.smartnutrition.dto.request.RecipeRequest;
import com.smartnutrition.dto.response.MealResponse;
import com.smartnutrition.dto.response.RecipeResponse;
import com.smartnutrition.dto.response.UserResponse;
import com.smartnutrition.service.SocialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/social")
@Tag(name = "Social & Friends Dashboard", description = "Endpoints for parent social interaction, recipes suggestions, friends requests, and social meal feed")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/friends/request/{friendId}")
    @Operation(summary = "Send a friend request to another parent")
    public ResponseEntity<?> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendId) {
        try {
            String message = socialService.sendFriendRequest(userDetails.getUsername(), friendId);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/friends/accept/{friendshipId}")
    @Operation(summary = "Accept an incoming friend request")
    public ResponseEntity<?> acceptFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long friendshipId) {
        try {
            String message = socialService.acceptFriendRequest(userDetails.getUsername(), friendshipId);
            return ResponseEntity.ok(Map.of("message", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/friends")
    @Operation(summary = "Get list of all accepted friends")
    public ResponseEntity<List<UserResponse>> getFriends(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<UserResponse> friends = socialService.getFriends(userDetails.getUsername());
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/friends/requests")
    @Operation(summary = "Get list of incoming pending friend requests")
    public ResponseEntity<List<UserResponse>> getIncomingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<UserResponse> requests = socialService.getIncomingRequests(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/recipes")
    @Operation(summary = "Share/suggest a healthy meal recipe with other parents")
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RecipeRequest request) {
        RecipeResponse response = socialService.createRecipe(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/recipes")
    @Operation(summary = "Retrieve all shared recipes ordered by newest first")
    public ResponseEntity<List<RecipeResponse>> getRecipes() {
        List<RecipeResponse> recipes = socialService.getRecipes();
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/friends/feed")
    @Operation(summary = "View recent meal logs of accepted friends (Social Feed)")
    public ResponseEntity<List<MealResponse>> getFriendsFeed(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<MealResponse> feed = socialService.getFriendsFeed(userDetails.getUsername());
        return ResponseEntity.ok(feed);
    }
}
