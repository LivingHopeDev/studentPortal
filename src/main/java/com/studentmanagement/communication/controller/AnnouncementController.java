package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/communication/announcements")
public class AnnouncementController {

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@Valid @RequestBody AnnouncementRequest request) {
        log.warn("Create announcement endpoint not implemented");
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.warn("List announcements endpoint not implemented");
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> get(@PathVariable UUID id) {
        log.warn("Get announcement endpoint not implemented for id: {}", id);
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AnnouncementRequest request) {
        log.warn("Update announcement endpoint not implemented for id: {}", id);
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        log.warn("Delete announcement endpoint not implemented for id: {}", id);
        return null;
    }

}
