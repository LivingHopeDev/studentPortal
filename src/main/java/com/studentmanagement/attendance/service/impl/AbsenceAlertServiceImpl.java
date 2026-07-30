package com.studentmanagement.attendance.service.impl;

import com.studentmanagement.attendance.dto.AbsenceAlertResponse;
import com.studentmanagement.attendance.dto.ThresholdRequest;
import com.studentmanagement.attendance.model.AbsenceAlert;
import com.studentmanagement.attendance.model.SubjectAttendance;
import com.studentmanagement.attendance.repository.AbsenceAlertRepository;
import com.studentmanagement.attendance.repository.SubjectAttendanceRepository;
import com.studentmanagement.attendance.service.AbsenceAlertService;
import com.studentmanagement.common.enums.AttendanceStatus;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AbsenceAlertServiceImpl implements AbsenceAlertService {

    private final AbsenceAlertRepository absenceAlertRepository;
    private final SubjectAttendanceRepository subjectAttendanceRepository;
    private final StudentRepository studentRepository;

    private static final double DEFAULT_THRESHOLD = 75.0;

    @Override
    @Transactional(readOnly = true)
    public List<AbsenceAlertResponse> getAlerts() {
        log.debug("Getting absence alerts");
        List<AbsenceAlert> alerts = absenceAlertRepository.findByResolvedFalse();
        List<AbsenceAlertResponse> responses = new ArrayList<>();

        for (AbsenceAlert alert : alerts) {
            responses.add(AbsenceAlertResponse.builder()
                    .studentId(alert.getStudent().getId())
                    .studentName(alert.getStudent().getUser() != null
                            ? alert.getStudent().getUser().getFullName()
                            : alert.getStudent().getFirstName() + " " + alert.getStudent().getLastName())
                    .attendancePercentage(alert.getCurrentPercentage() != null
                            ? alert.getCurrentPercentage().doubleValue() : 0.0)
                    .threshold(alert.getThreshold().doubleValue())
                    .build());
        }

        if (responses.isEmpty()) {
            responses.addAll(generateAlerts());
        }

        return responses;
    }

    @Override
    @Transactional
    public void updateAlertThreshold(ThresholdRequest request) {
        log.info("Updating alert threshold to: {}%", request.getThresholdPercentage());
        List<AbsenceAlert> unresolved = absenceAlertRepository.findByResolvedFalse();
        for (AbsenceAlert alert : unresolved) {
            alert.setThreshold(BigDecimal.valueOf(request.getThresholdPercentage()));
            absenceAlertRepository.save(alert);
        }
        log.info("Alert threshold updated for {} alerts", unresolved.size());
    }

    private List<AbsenceAlertResponse> generateAlerts() {
        log.debug("Generating absence alerts based on threshold: {}%", DEFAULT_THRESHOLD);
        List<AbsenceAlertResponse> alerts = new ArrayList<>();
        List<UUID> allStudentIds = subjectAttendanceRepository.findAll().stream()
                .map(sa -> sa.getStudent().getId())
                .distinct()
                .toList();

        for (UUID studentId : allStudentIds) {
            List<SubjectAttendance> records = subjectAttendanceRepository.findByStudentId(studentId);
            long total = records.size();
            long present = records.stream()
                    .filter(r -> r.getStatus() == AttendanceStatus.PRESENT)
                    .count();
            double percentage = total > 0 ? (double) present / total * 100.0 : 100.0;

            if (percentage < DEFAULT_THRESHOLD) {
                var student = studentRepository.findById(studentId).orElse(null);
                if (student == null) continue;

                AbsenceAlert alert = AbsenceAlert.builder()
                        .student(student)
                        .threshold(BigDecimal.valueOf(DEFAULT_THRESHOLD))
                        .currentPercentage(BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP))
                        .alertedAt(Instant.now())
                        .resolved(false)
                        .build();
                absenceAlertRepository.save(alert);

                alerts.add(AbsenceAlertResponse.builder()
                        .studentId(student.getId())
                        .studentName(student.getUser() != null
                                ? student.getUser().getFullName()
                                : student.getFirstName() + " " + student.getLastName())
                        .attendancePercentage(percentage)
                        .threshold(DEFAULT_THRESHOLD)
                        .build());
            }
        }

        log.info("Generated {} absence alerts", alerts.size());
        return alerts;
    }
}
