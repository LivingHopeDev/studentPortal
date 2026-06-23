package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communication/announcements")
public class AnnouncementController {

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(@Valid @RequestBody AnnouncementRequest request) {
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> get(@PathVariable UUID id) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody AnnouncementRequest request) {
        return null;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return null;
    }

}
