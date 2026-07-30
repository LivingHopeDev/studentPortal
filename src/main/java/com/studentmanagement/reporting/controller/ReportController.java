package com.studentmanagement.reporting.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.reporting.dto.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @GetMapping("/enrolment")
    public ResponseEntity<ApiResponse<EnrolmentReportResponse>> getEnrolmentReport() {
        log.warn("Get enrolment report endpoint not implemented");
        return null;
    }

    @GetMapping("/enrolment/export")
    public ResponseEntity<Resource> exportEnrolmentReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        log.warn("Export enrolment report endpoint not implemented");
        return null;
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceReportResponse>> getAttendanceReport() {
        log.warn("Get attendance report endpoint not implemented");
        return null;
    }

    @GetMapping("/attendance/export")
    public ResponseEntity<Resource> exportAttendanceReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        log.warn("Export attendance report endpoint not implemented");
        return null;
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<GradeReportResponse>> getGradeReport() {
        log.warn("Get grade report endpoint not implemented");
        return null;
    }

    @GetMapping("/grades/export")
    public ResponseEntity<Resource> exportGradeReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        log.warn("Export grade report endpoint not implemented");
        return null;
    }

    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<FeeReportResponse>> getFeeReport() {
        log.warn("Get fee report endpoint not implemented");
        return null;
    }

    @GetMapping("/fees/export")
    public ResponseEntity<Resource> exportFeeReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        log.warn("Export fee report endpoint not implemented");
        return null;
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<CustomReportResponse>> runCustomReport(@RequestBody CustomReportRequest request) {
        log.warn("Run custom report endpoint not implemented");
        return null;
    }

    @GetMapping("/custom/{jobId}")
    public ResponseEntity<ApiResponse<CustomReportResponse>> pollCustomReport(@PathVariable UUID jobId) {
        log.warn("Poll custom report endpoint not implemented for jobId: {}", jobId);
        return null;
    }

    @GetMapping("/custom/{jobId}/download")
    public ResponseEntity<Resource> downloadCustomReport(@PathVariable UUID jobId) {
        log.warn("Download custom report endpoint not implemented for jobId: {}", jobId);
        return null;
    }

}
