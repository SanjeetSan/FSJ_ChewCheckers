package com.smartnutrition.controller;

import com.smartnutrition.dto.request.LunchboxPresetRequest;
import com.smartnutrition.dto.response.LunchboxPresetResponse;
import com.smartnutrition.service.LunchboxPresetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parent/student/{studentId}/lunchbox-presets")
@Tag(name = "Lunchbox Presets", description = "Parent management of child lunchbox dimension presets")
public class LunchboxPresetController {

    private final LunchboxPresetService presetService;

    public LunchboxPresetController(LunchboxPresetService presetService) {
        this.presetService = presetService;
    }

    @GetMapping
    @Operation(summary = "Get all lunchbox presets configured for a child")
    public ResponseEntity<List<LunchboxPresetResponse>> getPresets(
            @PathVariable("studentId") Long studentId) {
        List<LunchboxPresetResponse> presets = presetService.getPresetsForStudent(studentId);
        return ResponseEntity.ok(presets);
    }

    @PostMapping
    @Operation(summary = "Create a new lunchbox preset for a child")
    public ResponseEntity<LunchboxPresetResponse> createPreset(
            @PathVariable("studentId") Long studentId,
            @Valid @RequestBody LunchboxPresetRequest request) {
        LunchboxPresetResponse response = presetService.createPreset(studentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{presetId}")
    @Operation(summary = "Update an existing lunchbox preset for a child")
    public ResponseEntity<LunchboxPresetResponse> updatePreset(
            @PathVariable("studentId") Long studentId,
            @PathVariable("presetId") Long presetId,
            @Valid @RequestBody LunchboxPresetRequest request) {
        LunchboxPresetResponse response = presetService.updatePreset(studentId, presetId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{presetId}/set-default")
    @Operation(summary = "Set a lunchbox preset as the child's default preset")
    public ResponseEntity<LunchboxPresetResponse> setDefaultPreset(
            @PathVariable("studentId") Long studentId,
            @PathVariable("presetId") Long presetId) {
        LunchboxPresetResponse response = presetService.setDefaultPreset(studentId, presetId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{presetId}")
    @Operation(summary = "Delete a lunchbox preset for a child")
    public ResponseEntity<Map<String, String>> deletePreset(
            @PathVariable("studentId") Long studentId,
            @PathVariable("presetId") Long presetId) {
        presetService.deletePreset(studentId, presetId);
        return ResponseEntity.ok(Map.of("message", "Lunchbox preset deleted successfully"));
    }
}
