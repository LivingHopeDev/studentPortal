package com.studentmanagement.communication.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThreadResponse {

    private UUID threadId;
    private String subject;
    private UUID otherParticipantId;
    private String otherParticipantName;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private int unreadCount;

}
