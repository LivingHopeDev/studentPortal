package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.GpaResponse;

import java.util.UUID;

public interface ResultService {

    GpaResponse calculateAndSaveResult(UUID studentId, UUID semesterId);
}
