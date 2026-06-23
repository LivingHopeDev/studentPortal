package com.studentmanagement.student.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String nationality;
    private String email;
    private String phone;
    private String studentNumber;
    private UUID programmeId;
    private String programmeName;
    private LocalDate admissionDate;
    private String status;

}
