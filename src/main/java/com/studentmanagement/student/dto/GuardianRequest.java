package com.studentmanagement.student.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianRequest {

    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String guardianRelationship;

}
