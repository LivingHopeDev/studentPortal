package com.studentmanagement.scheduling.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.scheduling.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules/exams")
public class ExamController {

    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@Valid @RequestBody ExamRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamResponse>>> listExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @PathVariable UUID id,
            @Valid @RequestBody ExamRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable UUID id) {
        return null;
    }

}
