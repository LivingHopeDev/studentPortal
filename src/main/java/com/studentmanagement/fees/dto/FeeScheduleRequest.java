package com.studentmanagement.fees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeScheduleRequest {

    @NotBlank
    private String name;

    @NotNull
    private Double amount;

    @NotBlank
    private String periodId;

}
