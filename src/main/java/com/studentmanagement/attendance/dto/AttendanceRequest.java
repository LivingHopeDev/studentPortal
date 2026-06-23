package com.studentmanagement.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {

    @NotNull
    private UUID studentId;

    private UUID sessionId;

    private String date;

    @NotBlank
    private String status;

    private String notes;

}
