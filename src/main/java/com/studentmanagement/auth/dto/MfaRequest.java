package com.studentmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MfaRequest {

    @NotBlank
    private String code;

}
