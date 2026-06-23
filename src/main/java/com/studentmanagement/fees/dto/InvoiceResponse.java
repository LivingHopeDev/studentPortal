package com.studentmanagement.fees.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private Double amount;
    private Double paidAmount;
    private Double balance;
    private String status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

}
