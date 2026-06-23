package com.studentmanagement.attendance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdRequest {

    @Min(0) @Max(100)
    private double thresholdPercentage;

}
