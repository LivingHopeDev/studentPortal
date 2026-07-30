package com.studentmanagement.reporting.service.impl;

import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.attendance.repository.AttendanceRepository;
import com.studentmanagement.common.enums.AttendanceStatus;
import com.studentmanagement.common.enums.InvoiceStatus;
import com.studentmanagement.common.enums.StudentStatus;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.fees.model.Invoice;
import com.studentmanagement.fees.repository.InvoiceRepository;
import com.studentmanagement.reporting.dto.*;
import com.studentmanagement.reporting.service.ReportService;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final InvoiceRepository invoiceRepository;

    private final ConcurrentHashMap<UUID, CustomReportJob> customJobStore = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public EnrolmentReportResponse getEnrolmentReport() {
        long total = studentRepository.count();
        long active = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long graduated = studentRepository.countByStatus(StudentStatus.GRADUATED);
        long suspended = studentRepository.countByStatus(StudentStatus.SUSPENDED);

        return EnrolmentReportResponse.builder()
                .totalStudents(total)
                .activeStudents(active)
                .graduated(graduated)
                .suspended(suspended)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportEnrolmentReport(String format, String from, String to, UUID programmeId, UUID classId) {
        List<Object[]> rows = new ArrayList<>();
        studentRepository.findAll().forEach(s -> rows.add(new Object[]{
                s.getStudentNo(), s.getFirstName() + " " + s.getLastName(),
                s.getStatus().name(), s.getProgramme() != null ? s.getProgramme().getName() : ""
        }));

        StringBuilder csv = new StringBuilder("Student No,Name,Status,Programme\n");
        for (Object[] row : rows) {
            csv.append(String.format("%s,%s,%s,%s\n", row[0], row[1], row[2], row[3]));
        }
        return csv.toString().getBytes();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceReportResponse getAttendanceReport() {
        long totalSessions = attendanceRepository.count();
        long present = attendanceRepository.countByStatus(AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByStatus(AttendanceStatus.ABSENT);
        double overallPercentage = totalSessions > 0
                ? Math.round((double) present / totalSessions * 10000.0) / 100.0
                : 0.0;

        return AttendanceReportResponse.builder()
                .overallPercentage(overallPercentage)
                .totalSessions(totalSessions)
                .totalPresent(present)
                .totalAbsent(absent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportAttendanceReport(String format, String from, String to, UUID programmeId, UUID classId) {
        AttendanceReportResponse report = getAttendanceReport();
        String csv = String.format(
                "Metric,Value\nTotal Sessions,%d\nTotal Present,%d\nTotal Absent,%d\nOverall Percentage,%s%%\n",
                report.getTotalSessions(), report.getTotalPresent(), report.getTotalAbsent(),
                report.getOverallPercentage());
        return csv.getBytes();
    }

    @Override
    @Transactional(readOnly = true)
    public GradeReportResponse getGradeReport() {
        List<Grade> allGrades = gradeRepository.findAll();
        long totalGrades = allGrades.size();

        double averageScore = allGrades.stream()
                .filter(g -> g.getScore() != null)
                .mapToDouble(g -> g.getScore().doubleValue())
                .average()
                .orElse(0.0);
        averageScore = Math.round(averageScore * 100.0) / 100.0;

        Map<String, Long> distribution = allGrades.stream()
                .filter(g -> g.getLetterGrade() != null)
                .collect(Collectors.groupingBy(Grade::getLetterGrade, Collectors.counting()));

        Map<String, Long> sortedDistribution = new LinkedHashMap<>();
        for (String letter : List.of("A", "B", "C", "D", "E", "F")) {
            sortedDistribution.put(letter, distribution.getOrDefault(letter, 0L));
        }

        return GradeReportResponse.builder()
                .totalGrades(totalGrades)
                .averageScore(averageScore)
                .gradeDistribution(sortedDistribution)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportGradeReport(String format, String from, String to, UUID programmeId, UUID classId) {
        GradeReportResponse report = getGradeReport();
        StringBuilder csv = new StringBuilder("Grade,Count\n");
        for (Map.Entry<String, Long> entry : report.getGradeDistribution().entrySet()) {
            csv.append(String.format("%s,%d\n", entry.getKey(), entry.getValue()));
        }
        csv.append(String.format("\nTotal Grades,%d\n", report.getTotalGrades()));
        csv.append(String.format("Average Score,%s\n", report.getAverageScore()));
        return csv.toString().getBytes();
    }

    @Override
    @Transactional(readOnly = true)
    public FeeReportResponse getFeeReport() {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        double totalExpected = allInvoices.stream()
                .mapToDouble(i -> i.getTotalAmount() != null ? i.getTotalAmount().doubleValue() : 0.0)
                .sum();
        double totalCollected = allInvoices.stream()
                .mapToDouble(i -> i.getPaidAmount() != null ? i.getPaidAmount().doubleValue() : 0.0)
                .sum();
        double outstanding = totalExpected - totalCollected;
        int paidCount = (int) allInvoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PAID).count();
        int unpaidCount = allInvoices.size() - paidCount;

        return FeeReportResponse.builder()
                .totalExpected(totalExpected)
                .totalCollected(totalCollected)
                .outstanding(outstanding)
                .paidCount(paidCount)
                .unpaidCount(unpaidCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportFeeReport(String format, String from, String to, UUID programmeId, UUID classId) {
        FeeReportResponse report = getFeeReport();
        String csv = String.format(
                "Metric,Value\nTotal Expected,%s\nTotal Collected,%s\nOutstanding,%s\nPaid Invoices,%d\nUnpaid Invoices,%d\n",
                report.getTotalExpected(), report.getTotalCollected(),
                report.getOutstanding(), report.getPaidCount(), report.getUnpaidCount());
        return csv.getBytes();
    }

    @Override
    @Transactional
    public CustomReportResponse runCustomReport(CustomReportRequest request) {
        UUID jobId = UUID.randomUUID();
        CustomReportJob job = new CustomReportJob(jobId, "PENDING");
        customJobStore.put(jobId, job);

        log.info("Custom report job created: id={}, type={}", jobId, request.getQueryType());

        simulateReportAsync(jobId, request);

        return CustomReportResponse.builder()
                .jobId(jobId)
                .status("PENDING")
                .downloadUrl("/api/v1/reports/custom/" + jobId + "/download")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomReportResponse pollCustomReport(UUID jobId) {
        CustomReportJob job = customJobStore.get(jobId);
        if (job == null) {
            throw new ResourceNotFoundException("CustomReportJob", "id", jobId);
        }
        return CustomReportResponse.builder()
                .jobId(jobId)
                .status(job.getStatus())
                .downloadUrl(job.getStatus().equals("COMPLETED")
                        ? "/api/v1/reports/custom/" + jobId + "/download" : null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadCustomReport(UUID jobId) {
        CustomReportJob job = customJobStore.get(jobId);
        if (job == null) {
            throw new ResourceNotFoundException("CustomReportJob", "id", jobId);
        }
        if (!"COMPLETED".equals(job.getStatus())) {
            throw new IllegalStateException("Report is not yet completed. Current status: " + job.getStatus());
        }
        return job.getResult();
    }

    private void simulateReportAsync(UUID jobId, CustomReportRequest request) {
        try {
            customJobStore.get(jobId).setStatus("RUNNING");

            Thread.sleep(2000);

            StringBuilder result = new StringBuilder();
            result.append("Custom Report\n");
            result.append("=============\n");
            result.append("Type: ").append(request.getQueryType()).append("\n");
            result.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n\n");

            if (request.getFilters() != null) {
                result.append("Filters:\n");
                for (Map.Entry<String, String> filter : request.getFilters().entrySet()) {
                    result.append("  ").append(filter.getKey()).append(": ").append(filter.getValue()).append("\n");
                }
                result.append("\n");
            }

            result.append("Report Data:\n");
            result.append("This is a simulated custom report. Actual data aggregation\n");
            result.append("would depend on the query type and filters provided.\n");

            CustomReportJob job = customJobStore.get(jobId);
            job.setStatus("COMPLETED");
            job.setResult(result.toString().getBytes());
            log.info("Custom report job completed: id={}", jobId);

        } catch (Exception e) {
            log.error("Custom report job failed: id={}", jobId, e);
            CustomReportJob job = customJobStore.get(jobId);
            if (job != null) {
                job.setStatus("FAILED");
                job.setResult(("Report generation failed: " + e.getMessage()).getBytes());
            }
        }
    }

    private static class CustomReportJob {
        private final UUID id;
        private String status;
        private byte[] result;

        CustomReportJob(UUID id, String status) {
            this.id = id;
            this.status = status;
        }

        UUID getId() { return id; }
        String getStatus() { return status; }
        byte[] getResult() { return result; }
        void setStatus(String status) { this.status = status; }
        void setResult(byte[] result) { this.result = result; }
    }
}
