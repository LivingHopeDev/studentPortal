package com.studentmanagement.staff.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private UUID id;
    private String employeeNo;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String role;
    private String photoUrl;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

}
