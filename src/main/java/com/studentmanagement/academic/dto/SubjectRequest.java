package com.studentmanagement.academic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private Integer credits;

}
