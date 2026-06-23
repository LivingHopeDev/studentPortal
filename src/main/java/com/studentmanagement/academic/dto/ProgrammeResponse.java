package com.studentmanagement.academic.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeResponse {

    private UUID id;
    private String name;
    private String code;

}
