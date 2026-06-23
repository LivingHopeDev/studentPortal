package com.studentmanagement.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentStatusRequest {

    @NotBlank
    private String status;

    private String reason;

}
