package com.studentmanagement.student.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentRequest {

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String email;
    private String phone;
    private UUID programmeId;
    private String admissionDate;
    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String guardianRelationship;

}
