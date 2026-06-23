package com.studentmanagement.communication.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {

    private UUID id;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

}
