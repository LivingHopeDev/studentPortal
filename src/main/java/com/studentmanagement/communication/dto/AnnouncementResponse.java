package com.studentmanagement.communication.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementResponse {

    private UUID id;
    private String title;
    private String content;
    private String audience;
    private String priority;
    private String createdBy;
    private LocalDateTime createdAt;

}
