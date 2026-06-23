package com.studentmanagement.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateRequest {

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

}
