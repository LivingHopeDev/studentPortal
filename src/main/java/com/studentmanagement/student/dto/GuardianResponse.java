package com.studentmanagement.student.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianResponse {

    private String name;
    private String phone;
    private String email;
    private String relationship;

}
