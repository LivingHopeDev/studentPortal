package com.studentmanagement.fees.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiationResponse {

    private String paymentReference;
    private String paymentUrl;
    private LocalDateTime expiresAt;

}
