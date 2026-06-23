package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.GradePublishRequest;
import com.studentmanagement.academic.dto.GradeRequest;
import com.studentmanagement.academic.dto.GradeResponse;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/grades")
public class GradeController {

    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponse>> submitGrade(@Valid @RequestBody GradeRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeResponse>>> listGrades(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID periodId) {
        return null;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getStudentGrades(@PathVariable UUID studentId) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable UUID id,
            @Valid @RequestBody GradeRequest request) {
        return null;
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<Void>> publishGrades(@Valid @RequestBody GradePublishRequest request) {
        return null;
    }

}
