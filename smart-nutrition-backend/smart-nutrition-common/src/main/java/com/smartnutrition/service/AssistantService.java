package com.smartnutrition.service;

import com.smartnutrition.dto.response.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AssistantService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @SuppressWarnings("unchecked")
    public ChatResponse getAIRecommendation(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return new ChatResponse("AI Nutrition Assistant is currently unavailable: Gemini API key is not configured. Please set the GEMINI_API_KEY environment variable on the server.");
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + apiKey;

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                    "parts", List.of(Map.of(
                        "text", "You are a professional pediatric nutritionist assistant for the Smart Nutrition App. Answer this parent query in a concise, friendly, and practical way, suggesting healthy recipes or nutrition adjustments: " + prompt
                    ))
                ))
            );

            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        String text = (String) parts.get(0).get("text");
                        return new ChatResponse(text);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Gemini API error: " + e.getMessage());
            return new ChatResponse("AI Nutrition Assistant is currently unavailable due to an API error: " + e.getMessage());
        }

        return new ChatResponse("AI Nutrition Assistant is currently unavailable: Empty response received from Gemini API.");
    }
}
