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
public class BulkInvoiceRequest {

    @NotNull
    private UUID scheduleId;

    @NotBlank
    private String periodId;

}
