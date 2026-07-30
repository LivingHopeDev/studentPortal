package com.studentmanagement.reporting.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.reporting.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports/dashboard")
public class DashboardController {

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardData>> getDashboard() {
        log.warn("Get dashboard endpoint not implemented");
        return null;
    }

}
