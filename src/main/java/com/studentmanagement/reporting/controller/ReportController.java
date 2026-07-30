package com.studentmanagement.reporting.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.reporting.dto.*;
import com.studentmanagement.reporting.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/enrolment")
    public ResponseEntity<ApiResponse<EnrolmentReportResponse>> getEnrolmentReport() {
        EnrolmentReportResponse response = reportService.getEnrolmentReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/enrolment/export")
    public ResponseEntity<Resource> exportEnrolmentReport(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        byte[] data = reportService.exportEnrolmentReport(format, from, to, programmeId, classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enrolment-report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/attendance")
    public ResponseEntity<ApiResponse<AttendanceReportResponse>> getAttendanceReport() {
        AttendanceReportResponse response = reportService.getAttendanceReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/attendance/export")
    public ResponseEntity<Resource> exportAttendanceReport(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        byte[] data = reportService.exportAttendanceReport(format, from, to, programmeId, classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance-report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<GradeReportResponse>> getGradeReport() {
        GradeReportResponse response = reportService.getGradeReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/grades/export")
    public ResponseEntity<Resource> exportGradeReport(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        byte[] data = reportService.exportGradeReport(format, from, to, programmeId, classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grade-report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/fees")
    public ResponseEntity<ApiResponse<FeeReportResponse>> getFeeReport() {
        FeeReportResponse response = reportService.getFeeReport();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/fees/export")
    public ResponseEntity<Resource> exportFeeReport(
            @RequestParam(defaultValue = "CSV") String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) UUID classId) {
        byte[] data = reportService.exportFeeReport(format, from, to, programmeId, classId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fee-report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(data));
    }

    @PostMapping("/custom")
    public ResponseEntity<ApiResponse<CustomReportResponse>> runCustomReport(@RequestBody CustomReportRequest request) {
        CustomReportResponse response = reportService.runCustomReport(request);
        return ResponseEntity.ok(ApiResponse.success("Custom report job created", response));
    }

    @GetMapping("/custom/{jobId}")
    public ResponseEntity<ApiResponse<CustomReportResponse>> pollCustomReport(@PathVariable UUID jobId) {
        CustomReportResponse response = reportService.pollCustomReport(jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/custom/{jobId}/download")
    public ResponseEntity<Resource> downloadCustomReport(@PathVariable UUID jobId) {
        byte[] data = reportService.downloadCustomReport(jobId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=custom-report-" + jobId + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(new ByteArrayResource(data));
    }
}
