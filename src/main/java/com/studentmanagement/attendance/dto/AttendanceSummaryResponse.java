package com.studentmanagement.attendance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceSummaryResponse {

    private String subjectName;
    private int totalSessions;
    private int attended;
    private int absent;
    private int late;
    private double percentage;

}
