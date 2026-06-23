package com.studentmanagement.fees.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private String paymentReference;
    private String gatewayReference;
    private String gateway;
    private Long amount;
    private String currency;
    private String status;
    private LocalDateTime paidAt;

}
