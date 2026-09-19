package com.smartnutrition.controller;

import com.smartnutrition.dto.request.PromptRequest;
import com.smartnutrition.dto.response.ChatResponse;
import com.smartnutrition.service.AssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
@Tag(name = "AI Nutrition Assistant", description = "Endpoints for parents to ask AI for diet, recipes, and nutrition suggestions")
public class AssistantController {

    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Ask the AI Nutrition Assistant for lunchbox ideas, cooking suggestions, or nutritional advice")
    public ResponseEntity<ChatResponse> askAssistant(@Valid @RequestBody PromptRequest request) {
        ChatResponse response = assistantService.getAIRecommendation(request.message());
        return ResponseEntity.ok(response);
    }
}
