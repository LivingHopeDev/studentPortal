package com.studentmanagement.fees.service.impl;

import com.studentmanagement.common.enums.InvoiceStatus;
import com.studentmanagement.common.enums.PaymentStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.fees.dto.*;
import com.studentmanagement.fees.model.Invoice;
import com.studentmanagement.fees.model.Payment;
import com.studentmanagement.fees.repository.InvoiceRepository;
import com.studentmanagement.fees.repository.PaymentRepository;
import com.studentmanagement.fees.service.PaymentService;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public PaymentInitiationResponse initiatePayment(InitiatePaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BadRequestException("Invoice is already " + invoice.getStatus().name().toLowerCase());
        }

        BigDecimal paymentAmount = BigDecimal.valueOf(request.getAmount());
        String reference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                + "-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(paymentAmount)
                .gateway(request.getGateway())
                .reference(reference)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("Payment initiated: ref={}, invoice={}, amount={}", reference, invoice.getId(), paymentAmount);

        String paymentUrl = "https://pay.example.com/checkout/" + reference;

        return PaymentInitiationResponse.builder()
                .paymentReference(reference)
                .paymentUrl(paymentUrl)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {
        log.info("Processing payment webhook: signature={}", signature);

        String reference = extractReferenceFromPayload(payload);
        String statusStr = extractStatusFromPayload(payload);

        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "reference", reference));

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid payment status: " + statusStr);
        }

        payment.setStatus(newStatus);
        if (newStatus == PaymentStatus.SUCCESS) {
            payment.setPaidAt(Instant.now());
        }
        paymentRepository.save(payment);

        if (newStatus == PaymentStatus.SUCCESS) {
            updateInvoiceAfterPayment(payment);
        }

        log.info("Payment {} updated to status: {}", reference, newStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return toPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public String downloadReceipt(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        Invoice invoice = payment.getInvoice();
        Student student = invoice.getStudent();

        StringBuilder receipt = new StringBuilder();
        receipt.append("=".repeat(60)).append("\n");
        receipt.append("                     PAYMENT RECEIPT\n");
        receipt.append("=".repeat(60)).append("\n\n");
        receipt.append("Receipt No:      ").append(payment.getReference()).append("\n");
        receipt.append("Date Paid:       ").append(payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A").append("\n");
        receipt.append("Payment Method:  ").append(payment.getGateway() != null ? payment.getGateway() : "N/A").append("\n\n");
        receipt.append("Student:         ").append(student.getFirstName()).append(" ").append(student.getLastName()).append("\n");
        receipt.append("Student No:      ").append(student.getStudentNo()).append("\n");
        receipt.append("Invoice No:      ").append(invoice.getInvoiceNo()).append("\n\n");
        receipt.append("-".repeat(60)).append("\n");
        receipt.append(String.format("Amount Paid:     %,.2f\n", payment.getAmount().doubleValue()));
        receipt.append("-".repeat(60)).append("\n");
        receipt.append(String.format("Total Amount:    %,.2f\n", invoice.getTotalAmount().doubleValue()));
        receipt.append(String.format("Paid Amount:     %,.2f\n", invoice.getPaidAmount().doubleValue()));
        receipt.append(String.format("Balance:         %,.2f\n", invoice.getBalance().doubleValue()));
        receipt.append("-".repeat(60)).append("\n\n");
        receipt.append("Status:          ").append(payment.getStatus().name()).append("\n");
        receipt.append("=".repeat(60)).append("\n");
        receipt.append("       Thank you for your payment!\n");
        receipt.append("=".repeat(60)).append("\n");

        return receipt.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public FeeSummaryResponse getFeeSummary() {
        List<Invoice> allInvoices = invoiceRepository.findAll();

        double totalExpected = allInvoices.stream()
                .mapToDouble(inv -> inv.getTotalAmount() != null ? inv.getTotalAmount().doubleValue() : 0.0)
                .sum();
        double totalCollected = allInvoices.stream()
                .mapToDouble(inv -> inv.getPaidAmount() != null ? inv.getPaidAmount().doubleValue() : 0.0)
                .sum();
        double outstanding = allInvoices.stream()
                .mapToDouble(inv -> inv.getBalance() != null ? inv.getBalance().doubleValue() : 0.0)
                .sum();
        int totalInvoices = allInvoices.size();
        int paidInvoices = (int) allInvoices.stream().filter(inv -> inv.getStatus() == InvoiceStatus.PAID).count();
        int unpaidInvoices = totalInvoices - paidInvoices;

        return FeeSummaryResponse.builder()
                .totalExpected(totalExpected)
                .totalCollected(totalCollected)
                .outstanding(outstanding)
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .unpaidInvoices(unpaidInvoices)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutstandingBalanceResponse> getOutstandingBalances() {
        List<Invoice> unpaidInvoices = invoiceRepository.findByStatus(InvoiceStatus.PENDING);
        unpaidInvoices.addAll(invoiceRepository.findByStatus(InvoiceStatus.PARTIAL));
        unpaidInvoices.addAll(invoiceRepository.findByStatus(InvoiceStatus.OVERDUE));

        Map<UUID, List<Invoice>> byStudent = unpaidInvoices.stream()
                .filter(inv -> inv.getStudent() != null && inv.getStudent().getId() != null)
                .collect(Collectors.groupingBy(inv -> inv.getStudent().getId()));

        List<OutstandingBalanceResponse> responses = new ArrayList<>();
        for (Map.Entry<UUID, List<Invoice>> entry : byStudent.entrySet()) {
            Student student = entry.getValue().get(0).getStudent();
            double totalOutstanding = entry.getValue().stream()
                    .mapToDouble(inv -> inv.getBalance() != null ? inv.getBalance().doubleValue() : 0.0)
                    .sum();

            responses.add(OutstandingBalanceResponse.builder()
                    .studentId(student.getId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .studentNumber(student.getStudentNo())
                    .outstandingAmount(totalOutstanding)
                    .build());
        }

        responses.sort((a, b) -> Double.compare(b.getOutstandingAmount(), a.getOutstandingAmount()));
        return responses;
    }

    private void updateInvoiceAfterPayment(Payment payment) {
        Invoice invoice = payment.getInvoice();
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(payment.getAmount());
        BigDecimal newBalance = invoice.getTotalAmount().subtract(newPaidAmount);

        invoice.setPaidAmount(newPaidAmount);
        invoice.setBalance(newBalance);

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setBalance(BigDecimal.ZERO);
        } else if (newPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIAL);
        }

        invoiceRepository.save(invoice);
        log.info("Invoice {} updated after payment: paid={}, balance={}, status={}",
                invoice.getId(), newPaidAmount, invoice.getBalance(), invoice.getStatus());
    }

    private String extractReferenceFromPayload(String payload) {
        if (payload != null && payload.contains("\"reference\"")) {
            int start = payload.indexOf("\"reference\"") + "\"reference\"".length();
            start = payload.indexOf("\"", start) + 1;
            int end = payload.indexOf("\"", start);
            if (start > 0 && end > start) {
                return payload.substring(start, end);
            }
        }
        throw new BadRequestException("Unable to extract reference from webhook payload");
    }

    private String extractStatusFromPayload(String payload) {
        if (payload != null && payload.contains("\"status\"")) {
            int start = payload.indexOf("\"status\"") + "\"status\"".length();
            start = payload.indexOf("\"", start) + 1;
            int end = payload.indexOf("\"", start);
            if (start > 0 && end > start) {
                return payload.substring(start, end);
            }
        }
        throw new BadRequestException("Unable to extract status from webhook payload");
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentReference(payment.getReference())
                .gatewayReference(payment.getGatewayReference())
                .gateway(payment.getGateway())
                .amount(payment.getAmount() != null ? payment.getAmount().longValue() : 0L)
                .currency("NGN")
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt() != null ? LocalDateTime.ofInstant(payment.getPaidAt(), ZoneId.systemDefault()) : null)
                .build();
    }
}
