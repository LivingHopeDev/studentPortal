package com.studentmanagement.reporting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceReportResponse {

    private double overallPercentage;
    private long totalSessions;
    private long totalPresent;
    private long totalAbsent;

}
