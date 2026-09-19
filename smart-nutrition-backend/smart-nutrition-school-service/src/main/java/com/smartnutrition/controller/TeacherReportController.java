package com.smartnutrition.controller;

import com.smartnutrition.dto.response.ClassReportResponse;
import com.smartnutrition.service.TeacherReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/reports")
@Tag(name = "Teacher Reports", description = "Endpoints for teachers to generate weekly and monthly class nutrition reports")
public class TeacherReportController {

    private final TeacherReportService teacherReportService;

    public TeacherReportController(TeacherReportService teacherReportService) {
        this.teacherReportService = teacherReportService;
    }

    @GetMapping("/weekly")
    @Operation(summary = "Get weekly nutrition report", description = "Generates a weekly nutrition summary for a given class code and start date.")
    public ResponseEntity<ClassReportResponse> getWeeklyReport(
            @Parameter(description = "The class code (e.g., CLS-3A)", required = true)
            @RequestParam("classCode") String classCode,
            @Parameter(description = "The week start date (YYYY-MM-DD)", required = false)
            @RequestParam(value = "startDate", required = false) String startDate) {
        return ResponseEntity.ok(teacherReportService.generateWeeklyReport(classCode, startDate));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Get monthly nutrition report", description = "Generates a monthly nutrition summary for a given class code, year, and month.")
    public ResponseEntity<ClassReportResponse> getMonthlyReport(
            @Parameter(description = "The class code (e.g., CLS-3A)", required = true)
            @RequestParam("classCode") String classCode,
            @Parameter(description = "Year (e.g., 2026)", required = false)
            @RequestParam(value = "year", required = false) Integer year,
            @Parameter(description = "Month 1-12 (e.g., 8)", required = false)
            @RequestParam(value = "month", required = false) Integer month) {
        return ResponseEntity.ok(teacherReportService.generateMonthlyReport(classCode, year, month));
    }
}
