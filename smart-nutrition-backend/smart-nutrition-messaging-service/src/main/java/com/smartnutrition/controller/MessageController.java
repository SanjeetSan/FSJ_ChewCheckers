package com.smartnutrition.controller;

import com.smartnutrition.dto.request.SendMessageRequest;
import com.smartnutrition.dto.response.MessageResponse;
import com.smartnutrition.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Direct Messages", description = "Endpoints for sending messages and retrieving chat history between parents and teachers")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Send a direct message to a user (parent, teacher, or friend)")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history/{contactId}")
    @Operation(summary = "Retrieve chat history between authenticated user and another contact")
    public ResponseEntity<List<MessageResponse>> getChatHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long contactId) {
        List<MessageResponse> history = messageService.getChatHistory(userDetails.getUsername(), contactId);
        return ResponseEntity.ok(history);
    }
}
