package com.studentmanagement.attendance.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbsenceAlertResponse {

    private UUID studentId;
    private String studentName;
    private double attendancePercentage;
    private double threshold;

}
