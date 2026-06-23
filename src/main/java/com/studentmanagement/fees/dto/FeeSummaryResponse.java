package com.studentmanagement.fees.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeSummaryResponse {

    private Double totalExpected;
    private Double totalCollected;
    private Double outstanding;
    private int totalInvoices;
    private int paidInvoices;
    private int unpaidInvoices;

}
