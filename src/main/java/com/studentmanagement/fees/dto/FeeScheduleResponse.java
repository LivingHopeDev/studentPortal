package com.studentmanagement.fees.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeScheduleResponse {

    private UUID id;
    private String name;
    private Double amount;
    private String periodId;

}
