package com.studentmanagement.fees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatePaymentRequest {

    @NotNull
    private UUID invoiceId;

    @NotNull
    private Long amount;

    @NotBlank
    private String gateway;

    @NotBlank
    private String callbackUrl;

}
