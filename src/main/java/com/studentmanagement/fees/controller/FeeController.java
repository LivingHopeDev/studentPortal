package com.studentmanagement.fees.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.fees.dto.*;
import com.studentmanagement.fees.service.FeeScheduleService;
import com.studentmanagement.fees.service.InvoiceService;
import com.studentmanagement.fees.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeScheduleService feeScheduleService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<List<FeeScheduleResponse>>> listSchedules() {
        List<FeeScheduleResponse> response = feeScheduleService.listSchedules();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<FeeScheduleResponse>> createSchedule(@Valid @RequestBody FeeScheduleRequest request) {
        FeeScheduleResponse response = feeScheduleService.createSchedule(request);
        return ResponseEntity.ok(ApiResponse.success("Fee schedule created", response));
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<FeeScheduleResponse>> updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody FeeScheduleRequest request) {
        FeeScheduleResponse response = feeScheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fee schedule updated", response));
    }

    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(@Valid @RequestBody GenerateInvoiceRequest request) {
        InvoiceResponse response = invoiceService.generateInvoice(request);
        return ResponseEntity.ok(ApiResponse.success("Invoice generated", response));
    }

    @PostMapping("/invoices/bulk")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> bulkGenerateInvoices(
            @Valid @RequestBody BulkInvoiceRequest request) {
        List<InvoiceResponse> response = invoiceService.bulkGenerateInvoices(request);
        return ResponseEntity.ok(ApiResponse.success("Bulk invoices generated", response));
    }

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> listInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) String status) {
        List<InvoiceResponse> response = invoiceService.listInvoices(page, size, studentId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable UUID id) {
        InvoiceResponse response = invoiceService.getInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/invoices/student/{studentId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getStudentInvoices(@PathVariable UUID studentId) {
        List<InvoiceResponse> response = invoiceService.getStudentInvoices(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/payments/initiate")
    public ResponseEntity<ApiResponse<PaymentInitiationResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        PaymentInitiationResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment initiated", response));
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Gateway-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        PaymentResponse response = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/receipts/{paymentId}")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable UUID paymentId) {
        String receipt = paymentService.downloadReceipt(paymentId);
        ByteArrayResource resource = new ByteArrayResource(receipt.getBytes());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=receipt-" + paymentId + ".txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @GetMapping("/report/summary")
    public ResponseEntity<ApiResponse<FeeSummaryResponse>> getFeeSummary() {
        FeeSummaryResponse response = paymentService.getFeeSummary();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/report/outstanding")
    public ResponseEntity<ApiResponse<List<OutstandingBalanceResponse>>> getOutstandingBalances() {
        List<OutstandingBalanceResponse> response = paymentService.getOutstandingBalances();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
