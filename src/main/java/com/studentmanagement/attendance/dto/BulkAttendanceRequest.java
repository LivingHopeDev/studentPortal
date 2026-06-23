package com.studentmanagement.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAttendanceRequest {

    @NotNull
    private UUID sessionId;

    @NotBlank
    private String date;

    @NotEmpty
    private List<Record> records;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Record {
        @NotNull
        private UUID studentId;
        @NotBlank
        private String status;
        private String notes;
    }

}
