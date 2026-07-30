package com.studentmanagement.scheduling.service;

import com.studentmanagement.scheduling.dto.ConflictCheckResponse;
import com.studentmanagement.scheduling.dto.TimetableRequest;
import com.studentmanagement.scheduling.dto.TimetableResponse;

import java.util.List;
import java.util.UUID;

public interface TimetableService {

    TimetableResponse createEntry(TimetableRequest request);

    List<TimetableResponse> listEntries(UUID classId, UUID staffId);

    TimetableResponse updateEntry(UUID id, TimetableRequest request);

    void deleteEntry(UUID id);

    List<TimetableResponse> getClassTimetable(UUID classId);

    List<TimetableResponse> getStaffSchedule(UUID staffId);

    ConflictCheckResponse checkConflicts(TimetableRequest request);

    String exportIcs(UUID id);
}
