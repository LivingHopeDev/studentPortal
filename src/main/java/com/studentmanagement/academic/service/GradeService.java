package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.GradePublishRequest;
import com.studentmanagement.academic.dto.GradeRequest;
import com.studentmanagement.academic.dto.GradeResponse;

import java.util.List;
import java.util.UUID;

public interface GradeService {

    GradeResponse submitGrade(GradeRequest request);

    List<GradeResponse> listGrades(UUID studentId, UUID subjectId, UUID periodId);

    List<GradeResponse> getStudentGrades(UUID studentId);

    GradeResponse updateGrade(UUID id, GradeRequest request);

    void publishGrades(GradePublishRequest request);
}
