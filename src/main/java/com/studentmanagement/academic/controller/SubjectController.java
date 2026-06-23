package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.SubjectRequest;
import com.studentmanagement.academic.dto.SubjectResponse;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/subjects")
public class SubjectController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> listSubjects() {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request) {
        return null;
    }

}
