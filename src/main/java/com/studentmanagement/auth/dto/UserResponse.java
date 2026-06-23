package com.studentmanagement.auth.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private String role;
    private boolean mfaEnabled;

}
