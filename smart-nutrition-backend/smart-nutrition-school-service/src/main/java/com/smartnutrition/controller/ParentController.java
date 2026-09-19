package com.smartnutrition.controller;

import com.smartnutrition.dto.request.CreateStudentRequest;
import com.smartnutrition.dto.request.LinkStudentRequest;
import com.smartnutrition.dto.response.ClassResponse;
import com.smartnutrition.dto.response.StudentResponse;
import com.smartnutrition.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/parent")
@Tag(name = "Parent", description = "Parent student linking and management endpoints")
public class ParentController {

    private final ParentService parentService;

    public ParentController(ParentService parentService) {
        this.parentService = parentService;
    }

    @PostMapping("/student")
    @Operation(summary = "Create a new student profile and link it to the authenticated parent")
    public ResponseEntity<StudentResponse> createStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = parentService.createAndLinkStudent(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/student/{studentId}")
    @Operation(summary = "Update student profile details (Name, Gender, DOB, Weight, Height, Blood Group, Class Code)")
    public ResponseEntity<StudentResponse> updateStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId,
            @RequestBody CreateStudentRequest request) {
        StudentResponse response = parentService.updateStudent(userDetails != null ? userDetails.getUsername() : null, studentId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/link-student")
    @Operation(summary = "Link parent account to a student using student code (e.g. STU-A001)")
    public ResponseEntity<StudentResponse> linkStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LinkStudentRequest request) {
        StudentResponse response = parentService.linkStudent(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/students")
    @Operation(summary = "Get all students linked to the authenticated parent")
    public ResponseEntity<List<StudentResponse>> getLinkedStudents(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<StudentResponse> students = parentService.getLinkedStudents(userDetails.getUsername());
        return ResponseEntity.ok(students);
    }

    @PutMapping("/student/{studentId}/class")
    @Operation(summary = "Link an existing student to a class using class code")
    public ResponseEntity<StudentResponse> updateStudentClass(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId,
            @RequestParam(value = "classCode", required = false) String classCode) {
        StudentResponse response = parentService.updateStudentClass(userDetails.getUsername(), studentId, classCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/student/{studentId}/lunchbox")
    @Operation(summary = "Update student lunchbox dimensions and calculate volume")
    public ResponseEntity<StudentResponse> updateStudentLunchbox(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId,
            @RequestBody java.util.Map<String, Object> requestBody) {
        BigDecimal length = requestBody.get("boxLength") != null ? new BigDecimal(requestBody.get("boxLength").toString()) : null;
        BigDecimal width = requestBody.get("boxWidth") != null ? new BigDecimal(requestBody.get("boxWidth").toString()) : null;
        BigDecimal depth = requestBody.get("boxDepth") != null ? new BigDecimal(requestBody.get("boxDepth").toString()) : null;
        String shape = requestBody.get("boxShape") != null ? requestBody.get("boxShape").toString() : null;
        Integer compartments = requestBody.get("boxCompartments") != null ? Integer.valueOf(requestBody.get("boxCompartments").toString()) : null;

        StudentResponse response = parentService.updateStudentLunchbox(userDetails.getUsername(), studentId, length, width, depth, shape, compartments);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/student/{studentId}")
    @Operation(summary = "Delete or unlink a student profile for authenticated parent")
    public ResponseEntity<java.util.Map<String, String>> deleteStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId) {
        parentService.deleteStudent(userDetails != null ? userDetails.getUsername() : null, studentId);
        return ResponseEntity.ok(java.util.Map.of("message", "Child profile deleted successfully"));
    }

    @GetMapping("/classes")
    @Operation(summary = "Get list of active classes for parent dropdown selection")
    public ResponseEntity<List<ClassResponse>> getActiveClasses() {
        List<ClassResponse> classes = parentService.getActiveClasses();
        return ResponseEntity.ok(classes);
    }
}
