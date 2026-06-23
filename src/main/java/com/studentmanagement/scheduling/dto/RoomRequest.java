package com.studentmanagement.scheduling.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NotBlank
    private String name;

    private String building;
    private Integer capacity;

}
