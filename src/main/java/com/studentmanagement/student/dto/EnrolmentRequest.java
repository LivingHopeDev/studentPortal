package com.studentmanagement.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrolmentRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String dateOfBirth;

    @NotBlank
    private String gender;

    private String nationality;

    private String email;

    private String phone;

    @NotNull
    private UUID programmeId;

    @NotBlank
    private String admissionDate;

    private String guardianName;
    private String guardianPhone;
    private String guardianEmail;
    private String guardianRelationship;

}
