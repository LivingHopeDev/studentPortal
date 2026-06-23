package com.studentmanagement.staff.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffScheduleResponse {

    private List<ScheduleEntry> entries;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScheduleEntry {
        private String day;
        private String startTime;
        private String endTime;
        private String subjectName;
        private String className;
    }

}
