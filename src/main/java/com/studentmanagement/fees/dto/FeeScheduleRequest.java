package com.studentmanagement.fees.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeScheduleRequest {

    @NotNull
    private UUID programmeId;

    @NotNull
    private UUID semesterId;

    private String components;

    @NotNull
    private Double totalAmount;
}
