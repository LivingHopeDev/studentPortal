package com.studentmanagement.reporting.service.impl;

import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.attendance.repository.AbsenceAlertRepository;
import com.studentmanagement.attendance.repository.AttendanceRepository;
import com.studentmanagement.common.enums.AttendanceStatus;
import com.studentmanagement.fees.model.Invoice;
import com.studentmanagement.fees.repository.InvoiceRepository;
import com.studentmanagement.reporting.dto.DashboardData;
import com.studentmanagement.reporting.service.DashboardService;
import com.studentmanagement.staff.repository.StaffRepository;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final InvoiceRepository invoiceRepository;
    private final AbsenceAlertRepository absenceAlertRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardData getDashboard() {
        long totalStudents = studentRepository.count();
        long totalStaff = staffRepository.count();
        long totalCourses = courseRepository.count();

        long totalSessions = attendanceRepository.count();
        long totalPresent = attendanceRepository.countByStatus(AttendanceStatus.PRESENT);
        double averageAttendance = totalSessions > 0
                ? Math.round((double) totalPresent / totalSessions * 10000.0) / 100.0
                : 0.0;

        double totalExpected = 0.0;
        double totalCollected = 0.0;
        for (Invoice inv : invoiceRepository.findAll()) {
            if (inv.getTotalAmount() != null) totalExpected += inv.getTotalAmount().doubleValue();
            if (inv.getPaidAmount() != null) totalCollected += inv.getPaidAmount().doubleValue();
        }
        double feeCollectionRate = totalExpected > 0
                ? Math.round(totalCollected / totalExpected * 10000.0) / 100.0
                : 0.0;

        long activeAlerts = absenceAlertRepository.count();

        return DashboardData.builder()
                .totalStudents(totalStudents)
                .totalStaff(totalStaff)
                .totalCourses(totalCourses)
                .averageAttendance(averageAttendance)
                .feeCollectionRate(feeCollectionRate)
                .activeAlerts(activeAlerts)
                .build();
    }
}
