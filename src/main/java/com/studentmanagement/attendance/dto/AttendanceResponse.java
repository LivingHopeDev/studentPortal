package com.studentmanagement.attendance.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {

    private UUID id;
    private UUID studentId;
    private UUID sessionId;
    private String date;
    private String status;
    private String notes;

}
