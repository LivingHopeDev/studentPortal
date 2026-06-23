package com.studentmanagement.student.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkImportResponse {

    private UUID jobId;
    private String status;
    private int total;
    private int success;
    private int failed;

}
