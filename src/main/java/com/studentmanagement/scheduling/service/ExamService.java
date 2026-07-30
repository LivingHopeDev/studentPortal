package com.studentmanagement.scheduling.service;

import com.studentmanagement.scheduling.dto.ExamRequest;
import com.studentmanagement.scheduling.dto.ExamResponse;

import java.util.List;
import java.util.UUID;

public interface ExamService {

    ExamResponse createExam(ExamRequest request);

    List<ExamResponse> listExams(int page, int size);

    ExamResponse updateExam(UUID id, ExamRequest request);

    void deleteExam(UUID id);
}
