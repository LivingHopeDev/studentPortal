package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.GpaResponse;
import com.studentmanagement.academic.service.GpaCalculator;
import com.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/academic/gpa")
@RequiredArgsConstructor
public class GpaController {

    private final GpaCalculator gpaCalculator;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<GpaResponse>> getGpa(@PathVariable UUID studentId) {
        log.info("Getting GPA for student: {}", studentId);
        GpaResponse response = gpaCalculator.getGpa(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
