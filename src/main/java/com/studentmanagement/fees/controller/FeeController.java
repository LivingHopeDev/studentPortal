package com.studentmanagement.fees.controller;

import com.studentmanagement.common.dto.ApiResponse;
import com.studentmanagement.fees.dto.*;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fees")
public class FeeController {

    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<List<FeeScheduleResponse>>> listSchedules() {
        return null;
    }

    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<FeeScheduleResponse>> createSchedule(@Valid @RequestBody FeeScheduleRequest request) {
        return null;
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<FeeScheduleResponse>> updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody FeeScheduleRequest request) {
        return null;
    }

    @PostMapping("/invoices")
    public ResponseEntity<ApiResponse<InvoiceResponse>> generateInvoice(@Valid @RequestBody GenerateInvoiceRequest request) {
        return null;
    }

    @PostMapping("/invoices/bulk")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> bulkGenerateInvoices(
            @Valid @RequestBody BulkInvoiceRequest request) {
        return null;
    }

    @GetMapping("/invoices")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> listInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) String status) {
        return null;
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoice(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/invoices/student/{studentId}")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getStudentInvoices(@PathVariable UUID studentId) {
        return null;
    }

    @PostMapping("/payments/initiate")
    public ResponseEntity<ApiResponse<PaymentInitiationResponse>> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        return null;
    }

    @PostMapping("/payments/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Gateway-Signature") String signature) {
        return null;
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable UUID id) {
        return null;
    }

    @GetMapping("/receipts/{paymentId}")
    public ResponseEntity<Resource> downloadReceipt(@PathVariable UUID paymentId) {
        return null;
    }

    @GetMapping("/report/summary")
    public ResponseEntity<ApiResponse<FeeSummaryResponse>> getFeeSummary() {
        return null;
    }

    @GetMapping("/report/outstanding")
    public ResponseEntity<ApiResponse<List<OutstandingBalanceResponse>>> getOutstandingBalances() {
        return null;
    }

}
