package com.studentmanagement.student.service;

import com.studentmanagement.student.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface StudentService {

    void init();

    StudentResponse enrolStudent(EnrolmentRequest request);

    StudentResponse activateStudent(UUID id, StudentStatusRequest request);

    StudentResponse updateStudent(UUID id, UpdateStudentRequest request);

    void deleteStudent(UUID id);

    StudentResponse getStudent(UUID id);

    List<StudentResponse> listStudents(int page, int size, String sort, String status,
                                        UUID programmeId, String search);

    BulkImportResponse bulkImport(MultipartFile file);

    BulkImportResponse getBulkImportStatus(UUID jobId);

    PhotoResponse uploadPhoto(UUID id, MultipartFile file);

    PhotoResponse getPhoto(UUID id);

    GuardianResponse getGuardian(UUID id);

    GuardianResponse upsertGuardian(UUID id, GuardianRequest request);

    AcademicSummaryResponse getAcademicSummary(UUID id);
}
