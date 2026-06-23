package com.studentmanagement.communication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyRequest {

    @NotBlank
    private String body;

}
