package com.studentmanagement.attendance.service;

import com.studentmanagement.attendance.dto.AttendanceResponse;

import java.util.List;
import java.util.UUID;

public interface ReportService {

    List<AttendanceResponse> getClassReport(UUID classId);

    byte[] getStudentReportPdf(UUID studentId);
}
