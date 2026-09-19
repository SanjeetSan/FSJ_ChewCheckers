package com.smartnutrition.controller;

import com.smartnutrition.dto.response.SchoolResponse;
import com.smartnutrition.repository.SchoolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@Tag(name = "School Operations", description = "School list retrieval")
public class SchoolController {

    private final SchoolRepository schoolRepository;

    public SchoolController(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @GetMapping
    @Operation(summary = "Get list of active schools")
    public ResponseEntity<List<SchoolResponse>> getActiveSchools() {
        List<SchoolResponse> schools = schoolRepository.findByIsActiveTrue().stream()
                .map(s -> new SchoolResponse(s.getId(), s.getName(), s.getAddress(), s.getAcademicYear()))
                .toList();
        return ResponseEntity.ok(schools);
    }
}
