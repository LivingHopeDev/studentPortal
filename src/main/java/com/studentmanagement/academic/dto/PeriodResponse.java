package com.studentmanagement.academic.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodResponse {

    private UUID id;
    private String name;
    private String startDate;
    private String endDate;
    private boolean current;

}
