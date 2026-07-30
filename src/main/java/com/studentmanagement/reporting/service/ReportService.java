package com.studentmanagement.reporting.service;

import com.studentmanagement.reporting.dto.*;

import java.util.UUID;

public interface ReportService {

    EnrolmentReportResponse getEnrolmentReport();

    byte[] exportEnrolmentReport(String format, String from, String to, UUID programmeId, UUID classId);

    AttendanceReportResponse getAttendanceReport();

    byte[] exportAttendanceReport(String format, String from, String to, UUID programmeId, UUID classId);

    GradeReportResponse getGradeReport();

    byte[] exportGradeReport(String format, String from, String to, UUID programmeId, UUID classId);

    FeeReportResponse getFeeReport();

    byte[] exportFeeReport(String format, String from, String to, UUID programmeId, UUID classId);

    CustomReportResponse runCustomReport(CustomReportRequest request);

    CustomReportResponse pollCustomReport(UUID jobId);

    byte[] downloadCustomReport(UUID jobId);
}
