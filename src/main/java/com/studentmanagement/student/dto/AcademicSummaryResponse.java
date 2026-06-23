package com.studentmanagement.student.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSummaryResponse {

    private double averageScore;
    private double attendanceRate;
    private List<SubjectSummary> subjects;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectSummary {
        private String subjectName;
        private Double score;
        private String grade;
        private double attendancePercentage;
    }

}
