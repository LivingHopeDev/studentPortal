package com.studentmanagement.attendance.controller;

import com.studentmanagement.attendance.dto.*;
import com.studentmanagement.attendance.service.AbsenceAlertService;
import com.studentmanagement.attendance.service.AttendanceService;
import com.studentmanagement.attendance.service.ReportService;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AbsenceAlertService absenceAlertService;
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<AttendanceResponse>> recordAttendance(@Valid @RequestBody AttendanceRequest request) {
        log.info("Recording attendance for student: {}", request.getStudentId());
        AttendanceResponse response = attendanceService.recordAttendance(request);
        return ResponseEntity.ok(ApiResponse.success("Attendance recorded", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> bulkRecordAttendance(
            @Valid @RequestBody BulkAttendanceRequest request) {
        log.info("Bulk recording attendance, {} records", request.getRecords().size());
        List<AttendanceResponse> response = attendanceService.bulkRecordAttendance(request);
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance recorded", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> listAttendance(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID sessionId,
            @RequestParam(required = false) String date) {
        log.debug("Listing attendance");
        List<AttendanceResponse> response = attendanceService.listAttendance(page, size, studentId, sessionId, date);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getStudentAttendance(@PathVariable UUID studentId) {
        log.debug("Getting attendance for student: {}", studentId);
        List<AttendanceResponse> response = attendanceService.getStudentAttendance(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<ApiResponse<List<AttendanceSummaryResponse>>> getStudentAttendanceSummary(
            @PathVariable UUID studentId) {
        log.debug("Getting attendance summary for student: {}", studentId);
        List<AttendanceSummaryResponse> response = attendanceService.getStudentAttendanceSummary(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable UUID id,
            @Valid @RequestBody AttendanceRequest request) {
        log.info("Updating attendance: {}", id);
        AttendanceResponse response = attendanceService.updateAttendance(id, request);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated", response));
    }

    @GetMapping("/report/class/{classId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getClassReport(@PathVariable UUID classId) {
        log.debug("Getting class attendance report: {}", classId);
        List<AttendanceResponse> response = reportService.getClassReport(classId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/report/student/{studentId}")
    public ResponseEntity<byte[]> getStudentReportPdf(@PathVariable UUID studentId) {
        log.info("Getting attendance PDF for student: {}", studentId);
        byte[] content = reportService.getStudentReportPdf(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=attendance-" + studentId + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<AbsenceAlertResponse>>> getAlerts() {
        log.debug("Getting absence alerts");
        List<AbsenceAlertResponse> response = absenceAlertService.getAlerts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/config/threshold")
    public ResponseEntity<ApiResponse<Void>> updateAlertThreshold(@Valid @RequestBody ThresholdRequest request) {
        log.info("Updating alert threshold to: {}%", request.getThresholdPercentage());
        absenceAlertService.updateAlertThreshold(request);
        return ResponseEntity.ok(ApiResponse.success("Alert threshold updated", null));
    }

}
