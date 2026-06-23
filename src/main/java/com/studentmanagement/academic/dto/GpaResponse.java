package com.studentmanagement.academic.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpaResponse {

    private Double gpa;
    private Double totalCredits;
    private Double totalPoints;

}
