package com.studentmanagement.fees.service;

import com.studentmanagement.common.enums.InvoiceStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.fees.dto.*;
import com.studentmanagement.fees.model.FeeSchedule;
import com.studentmanagement.fees.model.Invoice;
import com.studentmanagement.fees.repository.FeeScheduleRepository;
import com.studentmanagement.fees.repository.InvoiceRepository;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final FeeScheduleRepository feeScheduleRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public InvoiceResponse generateInvoice(GenerateInvoiceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        FeeSchedule schedule = feeScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeSchedule", "id", request.getScheduleId()));

        String invoiceNo = generateInvoiceNo();

        Invoice invoice = Invoice.builder()
                .student(student)
                .feeSchedule(schedule)
                .invoiceNo(invoiceNo)
                .totalAmount(schedule.getTotalAmount())
                .paidAmount(BigDecimal.ZERO)
                .balance(schedule.getTotalAmount())
                .status(InvoiceStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(30))
                .build();
        invoice = invoiceRepository.save(invoice);

        log.info("Invoice generated: {} for student: {}", invoiceNo, student.getId());
        return toResponse(invoice);
    }

    @Transactional
    public List<InvoiceResponse> bulkGenerateInvoices(BulkInvoiceRequest request) {
        FeeSchedule schedule = feeScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("FeeSchedule", "id", request.getScheduleId()));

        List<Student> students;
        if (schedule.getProgramme() != null) {
            students = studentRepository.findByProgrammeId(schedule.getProgramme().getId());
        } else {
            students = studentRepository.findAll();
        }

        List<InvoiceResponse> responses = new ArrayList<>();
        for (Student student : students) {
            String invoiceNo = generateInvoiceNo();
            Invoice invoice = Invoice.builder()
                    .student(student)
                    .feeSchedule(schedule)
                    .invoiceNo(invoiceNo)
                    .totalAmount(schedule.getTotalAmount())
                    .paidAmount(BigDecimal.ZERO)
                    .balance(schedule.getTotalAmount())
                    .status(InvoiceStatus.PENDING)
                    .dueDate(LocalDate.now().plusDays(30))
                    .build();
            invoice = invoiceRepository.save(invoice);
            responses.add(toResponse(invoice));
        }

        log.info("Bulk invoices generated: {} records for schedule: {}", responses.size(), request.getScheduleId());
        return responses;
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> listInvoices(int page, int size, UUID studentId, String status) {
        List<Invoice> invoices;

        if (studentId != null) {
            invoices = invoiceRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        } else if (status != null) {
            try {
                InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status.toUpperCase());
                invoices = invoiceRepository.findByStatusOrderByCreatedAtDesc(invoiceStatus);
            } catch (IllegalArgumentException e) {
                invoices = invoiceRepository.findAllByOrderByCreatedAtDesc();
            }
        } else {
            invoices = invoiceRepository.findAllByOrderByCreatedAtDesc();
        }

        return invoices.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getStudentInvoices(UUID studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        return invoiceRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private String generateInvoiceNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "INV-" + datePart + "-" + randomPart;
    }

    InvoiceResponse toResponse(Invoice invoice) {
        Student student = invoice.getStudent();
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .amount(invoice.getTotalAmount() != null ? invoice.getTotalAmount().doubleValue() : 0.0)
                .paidAmount(invoice.getPaidAmount() != null ? invoice.getPaidAmount().doubleValue() : 0.0)
                .balance(invoice.getBalance() != null ? invoice.getBalance().doubleValue() : 0.0)
                .status(invoice.getStatus().name())
                .dueDate(invoice.getDueDate() != null ? invoice.getDueDate().atStartOfDay() : null)
                .createdAt(invoice.getCreatedAt() != null ? LocalDateTime.ofInstant(invoice.getCreatedAt(), ZoneId.systemDefault()) : null)
                .build();
    }
}
