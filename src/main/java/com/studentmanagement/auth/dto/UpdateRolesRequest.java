package com.studentmanagement.auth.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRolesRequest {

    private Set<String> roles;

}
