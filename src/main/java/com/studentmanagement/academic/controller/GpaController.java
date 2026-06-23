package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.GpaResponse;
import com.studentmanagement.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/gpa")
public class GpaController {

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<GpaResponse>> getGpa(@PathVariable UUID studentId) {
        return null;
    }

}
