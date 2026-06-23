package com.studentmanagement.staff.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.staff.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffResponse>>> listStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaff(@PathVariable UUID id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody StaffRequest request) {
        return null;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaffStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StaffStatusRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable UUID id) {
        return null;
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StaffPhotoResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam MultipartFile file) {
        return null;
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<StaffScheduleResponse>> getSchedule(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<StaffResponse>> assignSubjects(
            @PathVariable UUID id,
            @Valid @RequestBody AssignSubjectsRequest request) {
        return null;
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<Void>> removeSubject(
            @PathVariable UUID id,
            @PathVariable UUID subjectId) {
        return null;
    }

}
