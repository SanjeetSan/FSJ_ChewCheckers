package com.smartnutrition.controller;

import com.smartnutrition.dto.response.ClassResponse;
import com.smartnutrition.dto.response.StudentResponse;
import com.smartnutrition.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@Tag(name = "Teacher Operations", description = "Teacher class overview and student roster management")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/classes")
    @Operation(summary = "Get teacher assigned classes", description = "Retrieves active classes managed by or accessible to teachers.")
    public ResponseEntity<List<ClassResponse>> getTeacherClasses(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(teacherService.getTeacherClasses(userDetails.getUsername()));
    }


    @PostMapping("/classes/{classId}/generate-code")
    @Operation(summary = "Generate a new unique class code for a class")
    public ResponseEntity<ClassResponse> generateNewClassCode(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("classId") Long classId) {
        ClassResponse response = teacherService.generateNewClassCode(classId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/students")
    @Operation(summary = "Get students in a class", description = "Retrieves student roster for a specific class code.")
    public ResponseEntity<List<StudentResponse>> getStudentsByClassCode(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "The class code (e.g. CLS-3A)", required = true)
            @RequestParam("classCode") String classCode) {
        return ResponseEntity.ok(teacherService.getStudentsByClassCodeAndTeacher(classCode, userDetails.getUsername()));
    }

    @PostMapping("/students/{studentId}/link")
    @Operation(summary = "Link an existing eligible student to the class")
    public ResponseEntity<StudentResponse> linkStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId,
            @RequestParam("classCode") String classCode) {
        return ResponseEntity.ok(teacherService.linkStudent(studentId, classCode, userDetails.getUsername()));
    }

    @DeleteMapping("/students/{studentId}/unlink")
    @Operation(summary = "Unlink an existing student from the class")
    public ResponseEntity<Void> unlinkStudent(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("studentId") Long studentId,
            @RequestParam("classCode") String classCode) {
        teacherService.unlinkStudent(studentId, classCode, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/eligible")
    @Operation(summary = "Get list of eligible students to link", description = "Retrieves active students who do not belong to any class.")
    public ResponseEntity<List<StudentResponse>> getEligibleStudents(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "query", required = false) String query) {
        return ResponseEntity.ok(teacherService.getEligibleStudents(query, userDetails.getUsername()));
    }
}
