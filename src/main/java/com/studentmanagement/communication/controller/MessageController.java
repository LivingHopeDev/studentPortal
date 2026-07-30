package com.studentmanagement.communication.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.communication.dto.MessageResponse;
import com.studentmanagement.communication.dto.ReplyRequest;
import com.studentmanagement.communication.dto.SendMessageRequest;
import com.studentmanagement.communication.dto.ThreadResponse;
import com.studentmanagement.communication.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/communication/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ThreadResponse>>> listThreads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ThreadResponse> response = messageService.listThreads(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageResponse>> send(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.send(request);
        return ResponseEntity.ok(ApiResponse.success("Message sent", response));
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getThread(@PathVariable UUID threadId) {
        List<MessageResponse> response = messageService.getThread(threadId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{threadId}/reply")
    public ResponseEntity<ApiResponse<MessageResponse>> reply(
            @PathVariable UUID threadId,
            @Valid @RequestBody ReplyRequest request) {
        MessageResponse response = messageService.reply(threadId, request);
        return ResponseEntity.ok(ApiResponse.success("Reply sent", response));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
        messageService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", null));
    }
}
