package com.studentmanagement.reporting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardData {

    private long totalStudents;
    private long totalStaff;
    private long totalCourses;
    private double averageAttendance;
    private double feeCollectionRate;
    private long activeAlerts;

}
