package com.studentmanagement.reporting.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.reporting.dto.DashboardData;
import com.studentmanagement.reporting.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardData>> getDashboard() {
        DashboardData response = dashboardService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
