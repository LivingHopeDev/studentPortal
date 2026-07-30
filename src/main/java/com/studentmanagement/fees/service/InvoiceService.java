package com.studentmanagement.fees.service;

import com.studentmanagement.fees.dto.*;
import com.studentmanagement.fees.model.Invoice;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse generateInvoice(GenerateInvoiceRequest request);

    List<InvoiceResponse> bulkGenerateInvoices(BulkInvoiceRequest request);

    List<InvoiceResponse> listInvoices(int page, int size, UUID studentId, String status);

    InvoiceResponse getInvoice(UUID id);

    List<InvoiceResponse> getStudentInvoices(UUID studentId);

    InvoiceResponse toResponse(Invoice invoice);
}
