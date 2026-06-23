package com.studentmanagement.reporting.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.reporting.dto.*;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @GetMapping("/enrolment")
    public ResponseEntity<ApiResponse<EnrolmentReportResponse>> getEnrolmentReport() {
        return null;
    }

    @GetMapping("/enrolment/export")
    public ResponseEntity<Resource> exportEnrolmentReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        return null;
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceReportResponse>> getAttendanceReport() {
        return null;
    }

    @GetMapping("/attendance/export")
    public ResponseEntity<Resource> exportAttendanceReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        return null;
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<GradeReportResponse>> getGradeReport() {
        return null;
    }

    @GetMapping("/grades/export")
    public ResponseEntity<Resource> exportGradeReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        return null;
    }

    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<FeeReportResponse>> getFeeReport() {
        return null;
    }

    @GetMapping("/fees/export")
    public ResponseEntity<Resource> exportFeeReport(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        return null;
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<CustomReportResponse>> runCustomReport(@RequestBody CustomReportRequest request) {
        return null;
    }

    @GetMapping("/custom/{jobId}")
    public ResponseEntity<ApiResponse<CustomReportResponse>> pollCustomReport(@PathVariable UUID jobId) {
        return null;
    }

    @GetMapping("/custom/{jobId}/download")
    public ResponseEntity<Resource> downloadCustomReport(@PathVariable UUID jobId) {
        return null;
    }

}
