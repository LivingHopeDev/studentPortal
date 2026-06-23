package com.studentmanagement.academic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeRequest {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID periodId;

    private Double score;

    private String letterGrade;

    private String remarks;

}
