package com.studentmanagement.communication.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateResponse {

    private UUID id;
    private String name;
    private String subject;
    private String body;

}
