package com.studentmanagement.scheduling.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConflictCheckResponse {

    private boolean hasConflict;
    private List<String> conflicts;

}
