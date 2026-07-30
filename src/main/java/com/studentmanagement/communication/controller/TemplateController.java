package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.NotificationTemplateRequest;
import com.studentmanagement.communication.dto.NotificationTemplateResponse;
import com.studentmanagement.communication.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/communication/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationTemplateResponse>>> list() {
        List<NotificationTemplateResponse> response = templateService.list();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NotificationTemplateResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplateRequest request) {
        NotificationTemplateResponse response = templateService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Template updated", response));
    }
}
