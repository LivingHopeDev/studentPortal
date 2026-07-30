package com.studentmanagement.academic.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeResponse {

    private UUID id;
    private String name;
    private String code;
    private Integer durationYears;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

}
