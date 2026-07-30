package com.studentmanagement.scheduling.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.scheduling.dto.ExamRequest;
import com.studentmanagement.scheduling.dto.ExamResponse;
import com.studentmanagement.scheduling.service.ExamService;
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
@RequestMapping("/api/v1/schedules/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@Valid @RequestBody ExamRequest request) {
        log.info("Creating exam for subject: {}", request.getSubjectId());
        ExamResponse response = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam scheduled successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamResponse>>> listExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Listing exams");
        List<ExamResponse> response = examService.listExams(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @PathVariable UUID id,
            @Valid @RequestBody ExamRequest request) {
        log.info("Updating exam: {}", id);
        ExamResponse response = examService.updateExam(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exam updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExam(@PathVariable UUID id) {
        log.info("Deleting exam: {}", id);
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam deleted successfully", null));
    }

}
