package com.studentmanagement.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

}
