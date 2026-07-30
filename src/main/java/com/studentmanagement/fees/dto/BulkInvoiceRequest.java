package com.studentmanagement.fees.dto;

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

    @NotNull
    private UUID semesterId;
}
