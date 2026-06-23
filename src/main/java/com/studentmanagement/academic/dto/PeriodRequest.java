package com.studentmanagement.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

}
