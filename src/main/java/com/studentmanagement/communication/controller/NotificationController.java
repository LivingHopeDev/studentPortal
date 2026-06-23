package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communication/notifications")
public class NotificationController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> listInbox(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        return null;
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearAll() {
        return null;
    }

}
