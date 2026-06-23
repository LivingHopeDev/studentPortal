package com.studentmanagement.communication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {

    @NotNull
    private UUID recipientId;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

}
