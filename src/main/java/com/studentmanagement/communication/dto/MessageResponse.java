package com.studentmanagement.communication.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private UUID id;
    private UUID threadId;
    private UUID senderId;
    private UUID recipientId;
    private String senderName;
    private String subject;
    private String body;
    private boolean read;
    private LocalDateTime sentAt;

}
