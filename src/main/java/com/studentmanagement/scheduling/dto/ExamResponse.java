package com.studentmanagement.scheduling.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {

    private UUID id;
    private UUID subjectId;
    private UUID classId;
    private String date;
    private String startTime;
    private String endTime;
    private UUID venueId;
    private String subjectName;

}
