package com.studentmanagement.staff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffStatusRequest {

    @NotBlank
    private String status;

}
