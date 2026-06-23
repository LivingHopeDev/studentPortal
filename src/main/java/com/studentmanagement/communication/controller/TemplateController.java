package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communication/templates")
public class TemplateController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationTemplateResponse>>> list() {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplateRequest request) {
        return null;
    }

}
