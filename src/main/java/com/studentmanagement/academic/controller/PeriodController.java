package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.PeriodRequest;
import com.studentmanagement.academic.dto.PeriodResponse;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/periods")
public class PeriodController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<PeriodResponse>>> listPeriods() {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PeriodResponse>> createPeriod(@Valid @RequestBody PeriodRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PeriodResponse>> updatePeriod(
            @PathVariable UUID id,
            @Valid @RequestBody PeriodRequest request) {
        return null;
    }

    @PatchMapping("/{id}/set-current")
    public ResponseEntity<ApiResponse<PeriodResponse>> setCurrentPeriod(@PathVariable UUID id) {
        return null;
    }

}
