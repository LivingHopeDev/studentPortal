package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.SubjectRequest;
import com.studentmanagement.academic.dto.SubjectResponse;
import com.studentmanagement.academic.service.SubjectService;
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
@RequestMapping("/api/v1/academic/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> listSubjects() {
        log.debug("Listing subjects");
        List<SubjectResponse> response = subjectService.listSubjects();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        log.info("Creating subject: {} ({})", request.getName(), request.getCode());
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request) {
        log.info("Updating subject: {}", id);
        SubjectResponse response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", response));
    }

}
