package com.studentmanagement.reporting.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomReportResponse {

    private UUID jobId;
    private String status;
    private String downloadUrl;

}
