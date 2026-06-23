package com.studentmanagement.academic.controller;

import com.studentmanagement.common.dto.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/transcripts")
public class TranscriptController {

    @GetMapping("/{studentId}")
    public ResponseEntity<Resource> downloadTranscript(@PathVariable UUID studentId) {
        return null;
    }

}
