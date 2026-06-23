package com.studentmanagement.reporting.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomReportRequest {

    private String queryType;
    private Map<String, String> filters;
    private String format;

}
