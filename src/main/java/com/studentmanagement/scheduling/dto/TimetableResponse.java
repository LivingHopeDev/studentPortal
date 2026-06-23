package com.studentmanagement.scheduling.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableResponse {

    private UUID id;
    private UUID classId;
    private UUID subjectId;
    private UUID staffId;
    private UUID venueId;
    private String day;
    private String startTime;
    private String endTime;
    private String subjectName;
    private String staffName;

}
