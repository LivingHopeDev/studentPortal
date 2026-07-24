package com.studentmanagement.student.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.student.dto.*;
import com.studentmanagement.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> enrolStudent(@Valid @RequestBody EnrolmentRequest request) {
        StudentResponse response = studentService.enrolStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student enrolled successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> listStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID programmeId,
            @RequestParam(required = false) String search) {
        List<StudentResponse> response = studentService.listStudents(page, size, sort, status, programmeId, search);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudent(@PathVariable UUID id) {
        StudentResponse response = studentService.getStudent(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody EnrolmentRequest request) {
        return null;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusRequest request) {
        StudentResponse response = studentService.activateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/bulk-import")
    public ResponseEntity<ApiResponse<BulkImportResponse>> bulkImport(@RequestParam MultipartFile file) {
        return null;
    }

    @GetMapping("/bulk-import/{jobId}")
    public ResponseEntity<ApiResponse<BulkImportResponse>> getBulkImportStatus(@PathVariable UUID jobId) {
        return null;
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PhotoResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam MultipartFile file) {
        return null;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<ApiResponse<PhotoResponse>> getPhoto(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/{id}/guardian")
    public ResponseEntity<ApiResponse<GuardianResponse>> getGuardian(@PathVariable UUID id) {
        return null;
    }

    @PostMapping("/{id}/guardian")
    public ResponseEntity<ApiResponse<GuardianResponse>> upsertGuardian(
            @PathVariable UUID id,
            @Valid @RequestBody GuardianRequest request) {
        return null;
    }

    @GetMapping("/{id}/academic-summary")
    public ResponseEntity<ApiResponse<AcademicSummaryResponse>> getAcademicSummary(@PathVariable UUID id) {
        return null;
    }
}
