package com.studentmanagement.reporting.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeReportResponse {

    private double totalExpected;
    private double totalCollected;
    private double outstanding;
    private int paidCount;
    private int unpaidCount;

}
