package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.PeriodRequest;
import com.studentmanagement.academic.dto.PeriodResponse;
import com.studentmanagement.academic.service.PeriodService;
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
@RequestMapping("/api/v1/academic/periods")
@RequiredArgsConstructor
public class PeriodController {

    private final PeriodService periodService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PeriodResponse>>> listPeriods() {
        log.debug("Listing periods");
        List<PeriodResponse> response = periodService.listPeriods();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PeriodResponse>> createPeriod(@Valid @RequestBody PeriodRequest request) {
        log.info("Creating period: {}", request.getName());
        PeriodResponse response = periodService.createPeriod(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Period created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PeriodResponse>> updatePeriod(
            @PathVariable UUID id,
            @Valid @RequestBody PeriodRequest request) {
        log.info("Updating period: {}", id);
        PeriodResponse response = periodService.updatePeriod(id, request);
        return ResponseEntity.ok(ApiResponse.success("Period updated successfully", response));
    }

    @PatchMapping("/{id}/set-current")
    public ResponseEntity<ApiResponse<PeriodResponse>> setCurrentPeriod(@PathVariable UUID id) {
        log.info("Setting current period: {}", id);
        PeriodResponse response = periodService.setCurrentPeriod(id);
        return ResponseEntity.ok(ApiResponse.success("Current period updated", response));
    }

}
