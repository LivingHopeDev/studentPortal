package com.studentmanagement.fees.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateInvoiceRequest {

    @NotNull
    private UUID studentId;

    @NotNull
    private UUID scheduleId;

}
