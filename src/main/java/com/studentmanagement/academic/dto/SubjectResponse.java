package com.studentmanagement.academic.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectResponse {

    private UUID id;
    private String name;
    private String code;
    private Integer credits;

}
