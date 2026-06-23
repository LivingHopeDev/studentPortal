package com.studentmanagement.staff.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String department;
    private String role;
    private String status;

}
