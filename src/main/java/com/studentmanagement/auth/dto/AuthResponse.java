package com.studentmanagement.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserResponse user;

}
