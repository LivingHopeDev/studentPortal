package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.ProgrammeRequest;
import com.studentmanagement.academic.dto.ProgrammeResponse;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/academic/programmes")
public class ProgrammeController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProgrammeResponse>>> listProgrammes() {
        return null;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProgrammeResponse>> createProgramme(@Valid @RequestBody ProgrammeRequest request) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgrammeResponse>> updateProgramme(
            @PathVariable UUID id,
            @Valid @RequestBody ProgrammeRequest request) {
        return null;
    }

}
