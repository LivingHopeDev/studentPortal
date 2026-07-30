package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.GradePublishRequest;
import com.studentmanagement.academic.dto.GradeRequest;
import com.studentmanagement.academic.dto.GradeResponse;
import com.studentmanagement.academic.service.GradeService;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/academic/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponse>> submitGrade(@Valid @RequestBody GradeRequest request) {
        log.info("Submitting grade for student: {}, subject: {}", request.getStudentId(), request.getSubjectId());
        GradeResponse response = gradeService.submitGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grade submitted successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeResponse>>> listGrades(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID subjectId,
            @RequestParam(required = false) UUID periodId) {
        log.debug("Listing grades - student: {}, subject: {}, period: {}", studentId, subjectId, periodId);
        List<GradeResponse> response = gradeService.listGrades(studentId, subjectId, periodId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getStudentGrades(@PathVariable UUID studentId) {
        log.debug("Getting grades for student: {}", studentId);
        List<GradeResponse> response = gradeService.getStudentGrades(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable UUID id,
            @Valid @RequestBody GradeRequest request) {
        log.info("Updating grade: {}", id);
        GradeResponse response = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(ApiResponse.success("Grade updated successfully", response));
    }

    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<Void>> publishGrades(@Valid @RequestBody GradePublishRequest request) {
        log.info("Publishing grades for period: {}, subject: {}", request.getPeriodId(), request.getSubjectId());
        gradeService.publishGrades(request);
        return ResponseEntity.ok(ApiResponse.success("Grades published successfully", null));
    }

}
