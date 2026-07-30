package com.studentmanagement.student.service;

import com.studentmanagement.academic.dto.ProgrammeRequest;
import com.studentmanagement.academic.dto.ProgrammeResponse;

import java.util.List;
import java.util.UUID;

public interface ProgrammeService {

    ProgrammeResponse updateProgramme(UUID id, ProgrammeRequest request);

    List<ProgrammeResponse> listProgrammes();

    ProgrammeResponse createProgramme(ProgrammeRequest request);
}
