package com.studentmanagement.fees.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutstandingBalanceResponse {

    private UUID studentId;
    private String studentName;
    private String studentNumber;
    private Double outstandingAmount;

}
