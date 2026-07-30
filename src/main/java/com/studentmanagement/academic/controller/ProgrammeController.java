package com.studentmanagement.academic.controller;

import com.studentmanagement.academic.dto.ProgrammeRequest;
import com.studentmanagement.academic.dto.ProgrammeResponse;
import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.student.service.ProgrammeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/academic/programmes")
@RequiredArgsConstructor
public class ProgrammeController {

    private final ProgrammeService programmeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProgrammeResponse>>> listProgrammes() {
        log.debug("Listing programmes");
        List<ProgrammeResponse> programmes = programmeService.listProgrammes();
        return ResponseEntity.ok(ApiResponse.success(programmes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProgrammeResponse>> createProgramme(@Valid @RequestBody ProgrammeRequest request) {
        log.info("Creating programme: {} ({})", request.getName(), request.getCode());
        ProgrammeResponse response = programmeService.createProgramme(request);
        log.info("Programme created: id={}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Programme created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProgrammeResponse>> updateProgramme(
            @PathVariable UUID id,
            @Valid @RequestBody ProgrammeRequest request) {
        log.info("Updating programme: {}", id);
        ProgrammeResponse response = programmeService.updateProgramme(id, request);
        return ResponseEntity.ok(ApiResponse.success("Programme updated successfully", response));
    }

}
