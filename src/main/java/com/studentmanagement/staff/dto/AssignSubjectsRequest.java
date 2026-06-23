package com.studentmanagement.staff.dto;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignSubjectsRequest {

    private Set<UUID> subjectIds;

}
