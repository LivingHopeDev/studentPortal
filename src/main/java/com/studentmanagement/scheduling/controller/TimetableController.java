package com.studentmanagement.scheduling.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.scheduling.dto.ConflictCheckResponse;
import com.studentmanagement.scheduling.dto.TimetableRequest;
import com.studentmanagement.scheduling.dto.TimetableResponse;
import com.studentmanagement.scheduling.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedules/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping
    public ResponseEntity<ApiResponse<TimetableResponse>> createEntry(@Valid @RequestBody TimetableRequest request) {
        log.info("Creating timetable entry");
        TimetableResponse response = timetableService.createEntry(request);
        return ResponseEntity.ok(ApiResponse.success("Timetable entry created", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> listEntries(
            @RequestParam(required = false) UUID classId,
            @RequestParam(required = false) UUID staffId) {
        log.debug("Listing timetable entries");
        List<TimetableResponse> response = timetableService.listEntries(classId, staffId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TimetableResponse>> updateEntry(
            @PathVariable UUID id,
            @Valid @RequestBody TimetableRequest request) {
        log.info("Updating timetable entry: {}", id);
        TimetableResponse response = timetableService.updateEntry(id, request);
        return ResponseEntity.ok(ApiResponse.success("Timetable entry updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEntry(@PathVariable UUID id) {
        log.info("Deleting timetable entry: {}", id);
        timetableService.deleteEntry(id);
        return ResponseEntity.ok(ApiResponse.success("Timetable entry deleted", null));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getClassTimetable(@PathVariable UUID classId) {
        log.debug("Getting class timetable: {}", classId);
        List<TimetableResponse> response = timetableService.getClassTimetable(classId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse<List<TimetableResponse>>> getStaffSchedule(@PathVariable UUID staffId) {
        log.debug("Getting staff schedule: {}", staffId);
        List<TimetableResponse> response = timetableService.getStaffSchedule(staffId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/check-conflicts")
    public ResponseEntity<ApiResponse<ConflictCheckResponse>> checkConflicts(@Valid @RequestBody TimetableRequest request) {
        log.debug("Checking conflicts");
        ConflictCheckResponse response = timetableService.checkConflicts(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/export.ics")
    public ResponseEntity<String> exportIcs(@PathVariable UUID id) {
        log.info("Exporting ICS for entry: {}", id);
        String ics = timetableService.exportIcs(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=timetable-" + id + ".ics")
                .contentType(MediaType.parseMediaType("text/calendar; charset=utf-8"))
                .body(ics);
    }

}
