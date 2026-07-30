package com.studentmanagement.academic.service;

import java.util.UUID;

public interface TranscriptService {

    byte[] generateTranscript(UUID studentId);
}
