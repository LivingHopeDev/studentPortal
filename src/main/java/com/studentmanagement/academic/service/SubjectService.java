package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.SubjectRequest;
import com.studentmanagement.academic.dto.SubjectResponse;

import java.util.List;
import java.util.UUID;

public interface SubjectService {

    List<SubjectResponse> listSubjects();

    SubjectResponse createSubject(SubjectRequest request);

    SubjectResponse updateSubject(UUID id, SubjectRequest request);
}
