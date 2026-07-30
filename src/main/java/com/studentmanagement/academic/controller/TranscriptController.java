package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.service.TranscriptService;
import com.studentmanagement.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/academic/transcripts")
@RequiredArgsConstructor
public class TranscriptController {

    private final TranscriptService transcriptService;

    @GetMapping("/{studentId}")
    public ResponseEntity<byte[]> downloadTranscript(@PathVariable UUID studentId) {
        log.info("Downloading transcript for student: {}", studentId);
        byte[] content = transcriptService.generateTranscript(studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript-" + studentId + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(content);
    }

}
