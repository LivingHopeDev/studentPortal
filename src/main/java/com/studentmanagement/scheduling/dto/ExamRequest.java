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
public class ExamRequest {

    @NotNull
    private UUID subjectId;

    @NotNull
    private UUID classId;

    @NotBlank
    private String date;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

    @NotNull
    private UUID venueId;

}
