package com.studentmanagement.student.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.student.dto.*;
import com.studentmanagement.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> enrolStudent(@Valid @RequestBody EnrolmentRequest request) {
        log.info("Enrolling student: {} {}", request.getFirstName(), request.getLastName());
        StudentResponse response = studentService.enrolStudent(request);
        log.info("Student enrolled successfully with id: {}", response.getId());
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
        log.debug("Listing students - page: {}, size: {}, status: {}, programmeId: {}, search: {}",
                page, size, status, programmeId, search);
        List<StudentResponse> response = studentService.listStudents(page, size, sort, status, programmeId, search);
        log.debug("Found {} students", response.size());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudent(@PathVariable UUID id) {
        log.debug("Getting student by id: {}", id);
        StudentResponse response = studentService.getStudent(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStudentRequest request) {
        log.info("Updating student: {}", id);
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StudentStatusRequest request) {
        log.info("Updating status for student: {} to {}", id, request.getStatus());
        StudentResponse response = studentService.activateStudent(id, request);
        log.info("Student {} status updated to {}", id, response.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Student status updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable UUID id) {
        log.info("Deleting student: {}", id);
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    @PostMapping("/bulk-import")
    public ResponseEntity<ApiResponse<BulkImportResponse>> bulkImport(@RequestParam MultipartFile file) {
        log.info("Bulk import requested, file: {}", file.getOriginalFilename());
        BulkImportResponse response = studentService.bulkImport(file);
        return ResponseEntity.ok(ApiResponse.success("Bulk import completed", response));
    }

    @GetMapping("/bulk-import/{jobId}")
    public ResponseEntity<ApiResponse<BulkImportResponse>> getBulkImportStatus(@PathVariable UUID jobId) {
        log.info("Bulk import status requested for jobId: {}", jobId);
        BulkImportResponse response = studentService.getBulkImportStatus(jobId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PhotoResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam MultipartFile file) {
        log.info("Photo upload requested for student: {}", id);
        PhotoResponse response = studentService.uploadPhoto(id, file);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", response));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<ApiResponse<PhotoResponse>> getPhoto(@PathVariable UUID id) {
        log.info("Getting photo for student: {}", id);
        PhotoResponse response = studentService.getPhoto(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/guardian")
    public ResponseEntity<ApiResponse<GuardianResponse>> getGuardian(@PathVariable UUID id) {
        log.info("Getting guardian for student: {}", id);
        GuardianResponse response = studentService.getGuardian(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{id}/guardian")
    public ResponseEntity<ApiResponse<GuardianResponse>> upsertGuardian(
            @PathVariable UUID id,
            @Valid @RequestBody GuardianRequest request) {
        log.info("Upserting guardian for student: {}", id);
        GuardianResponse response = studentService.upsertGuardian(id, request);
        return ResponseEntity.ok(ApiResponse.success("Guardian saved successfully", response));
    }

    @GetMapping("/{id}/academic-summary")
    public ResponseEntity<ApiResponse<AcademicSummaryResponse>> getAcademicSummary(@PathVariable UUID id) {
        log.info("Getting academic summary for student: {}", id);
        AcademicSummaryResponse response = studentService.getAcademicSummary(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
