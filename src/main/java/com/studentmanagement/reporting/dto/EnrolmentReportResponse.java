package com.studentmanagement.reporting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolmentReportResponse {

    private long totalStudents;
    private long activeStudents;
    private long graduated;
    private long suspended;

}
