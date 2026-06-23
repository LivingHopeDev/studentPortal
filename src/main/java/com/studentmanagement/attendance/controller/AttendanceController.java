package com.studentmanagement.attendance.controller;

import com.studentmanagement.attendance.dto.*;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponse>> recordAttendance(@Valid @RequestBody AttendanceRequest request) {
        return null;
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> bulkRecordAttendance(
            @Valid @RequestBody BulkAttendanceRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> listAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID sessionId,
            @RequestParam(required = false) String date) {
        return null;
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getStudentAttendance(@PathVariable UUID studentId) {
        return null;
    }

    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryResponse>>> getStudentAttendanceSummary(
            @PathVariable UUID studentId) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request) {
        return null;
    }

    @GetMapping("/report/class/{classId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getClassReport(@PathVariable UUID classId) {
        return null;
    }

    @GetMapping("/report/student/{studentId}")
    public ResponseEntity<Resource> getStudentReportPdf(@PathVariable UUID studentId) {
        return null;
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<AbsenceAlertResponse>>> getAlerts() {
        return null;
    }

    @PutMapping("/config/threshold")
    public ResponseEntity<ApiResponse<Void>> updateAlertThreshold(@Valid @RequestBody ThresholdRequest request) {
        return null;
    }

}
