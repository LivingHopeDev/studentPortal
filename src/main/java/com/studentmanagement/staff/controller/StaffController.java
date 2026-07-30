package com.studentmanagement.staff.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.staff.dto.*;
import com.studentmanagement.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(@Valid @RequestBody StaffRequest request) {
        log.info("Creating staff: {} {} <{}>", request.getFirstName(), request.getLastName(), request.getEmail());
        StaffResponse response = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Staff created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffResponse>>> listStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("Listing staff - page: {}, size: {}", page, size);
        List<StaffResponse> response = staffService.listStaff(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaff(@PathVariable UUID id) {
        log.debug("Fetching staff: {}", id);
        StaffResponse response = staffService.getStaff(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody StaffRequest request) {
        log.info("Updating staff: {}", id);
        StaffResponse response = staffService.updateStaff(id, request);
        return ResponseEntity.ok(ApiResponse.success("Staff updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStaffStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StaffStatusRequest request) {
        log.info("Updating status for staff: {} to {}", id, request.getStatus());
        StaffResponse response = staffService.updateStaffStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Staff status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable UUID id) {
        log.info("Deleting staff: {}", id);
        staffService.deleteStaff(id);
        return ResponseEntity.ok(ApiResponse.success("Staff deleted successfully", null));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<StaffPhotoResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam MultipartFile file) {
        log.warn("Staff photo upload endpoint not implemented for id: {}", id);
        return null;
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<StaffScheduleResponse>> getSchedule(@PathVariable UUID id) {
        log.warn("Get staff schedule endpoint not implemented for id: {}", id);
        return null;
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<ApiResponse<StaffResponse>> assignSubjects(
            @PathVariable UUID id,
            @Valid @RequestBody AssignSubjectsRequest request) {
        log.warn("Assign subjects endpoint not implemented for id: {}", id);
        return null;
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<ApiResponse<Void>> removeSubject(
            @PathVariable UUID id,
            @PathVariable UUID subjectId) {
        log.warn("Remove subject endpoint not implemented for id: {}", id);
        return null;
    }

}
