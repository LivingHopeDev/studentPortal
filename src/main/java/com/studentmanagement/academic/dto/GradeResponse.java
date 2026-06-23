package com.studentmanagement.academic.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeResponse {

    private UUID id;
    private UUID studentId;
    private UUID subjectId;
    private UUID periodId;
    private Double score;
    private String letterGrade;
    private String remarks;

}
