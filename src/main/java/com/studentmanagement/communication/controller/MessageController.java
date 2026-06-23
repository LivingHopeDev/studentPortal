package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/communication/messages")
public class MessageController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThreadResponse>>> listThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> send(@Valid @RequestBody SendMessageRequest request) {
        return null;
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getThread(@PathVariable UUID threadId) {
        return null;
    }

    @PostMapping("/{threadId}/reply")
    public ResponseEntity<ApiResponse<MessageResponse>> reply(
            @PathVariable UUID threadId,
            @Valid @RequestBody ReplyRequest request) {
        return null;
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        return null;
    }

}
