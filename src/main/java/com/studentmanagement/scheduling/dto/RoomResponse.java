package com.studentmanagement.scheduling.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private UUID id;
    private String name;
    private String building;
    private Integer capacity;

}
