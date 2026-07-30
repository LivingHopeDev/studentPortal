package com.studentmanagement.fees.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeScheduleResponse {

    private UUID id;
    private UUID programmeId;
    private String programmeName;
    private UUID semesterId;
    private String semesterName;
    private String components;
    private Double totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
}
