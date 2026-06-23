package com.studentmanagement.reporting.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeReportResponse {

    private long totalGrades;
    private double averageScore;
    private Map<String, Long> gradeDistribution;

}
