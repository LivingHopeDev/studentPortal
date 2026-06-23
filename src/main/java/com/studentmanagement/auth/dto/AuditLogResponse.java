package com.studentmanagement.auth.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private String action;
    private String performedBy;
    private LocalDateTime timestamp;
    private String details;

}
