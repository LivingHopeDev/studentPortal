package com.studentmanagement.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableRequest {

    @NotNull
    private UUID classId;

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID staffId;

    @NotNull
    private UUID venueId;

    @NotBlank
    private String day;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

}
