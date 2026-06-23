package com.studentmanagement.academic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradePublishRequest {

    @NotNull
    private UUID periodId;

    @NotNull
    private UUID subjectId;

}
