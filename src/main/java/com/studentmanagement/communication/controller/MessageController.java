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
@RequestMapping("/api/v1/communication/messages")
public class MessageController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThreadResponse>>> listThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.warn("List threads endpoint not implemented");
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> send(@Valid @RequestBody SendMessageRequest request) {
        log.warn("Send message endpoint not implemented");
        return null;
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getThread(@PathVariable UUID threadId) {
        log.warn("Get thread endpoint not implemented for id: {}", threadId);
        return null;
    }

    @PostMapping("/{threadId}/reply")
    public ResponseEntity<ApiResponse<MessageResponse>> reply(
            @PathVariable UUID threadId,
            @Valid @RequestBody ReplyRequest request) {
        log.warn("Reply to thread endpoint not implemented for id: {}", threadId);
        return null;
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        log.warn("Mark message as read endpoint not implemented for id: {}", id);
        return null;
    }

}
