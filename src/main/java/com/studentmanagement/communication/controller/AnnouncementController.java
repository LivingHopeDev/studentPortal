package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.AnnouncementRequest;
import com.studentmanagement.communication.dto.AnnouncementResponse;
import com.studentmanagement.communication.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/communication/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Announcement created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AnnouncementResponse> response = announcementService.list();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> get(@PathVariable UUID id) {
        AnnouncementResponse response = announcementService.get(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Announcement updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Announcement deleted", null));
    }
}
