package com.studentmanagement.attendance.service;

import com.studentmanagement.attendance.dto.AttendanceRequest;
import com.studentmanagement.attendance.dto.AttendanceResponse;
import com.studentmanagement.attendance.dto.AttendanceSummaryResponse;
import com.studentmanagement.attendance.dto.BulkAttendanceRequest;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {

    AttendanceResponse recordAttendance(AttendanceRequest request);

    List<AttendanceResponse> bulkRecordAttendance(BulkAttendanceRequest request);

    List<AttendanceResponse> listAttendance(int page, int size, UUID studentId, UUID sessionId, String date);

    List<AttendanceResponse> getStudentAttendance(UUID studentId);

    List<AttendanceSummaryResponse> getStudentAttendanceSummary(UUID studentId);

    AttendanceResponse updateAttendance(UUID id, AttendanceRequest request);
}
