package com.studentmanagement.attendance.service.impl;

import com.studentmanagement.attendance.dto.AttendanceRequest;
import com.studentmanagement.attendance.dto.AttendanceResponse;
import com.studentmanagement.attendance.dto.AttendanceSummaryResponse;
import com.studentmanagement.attendance.dto.BulkAttendanceRequest;
import com.studentmanagement.attendance.model.Attendance;
import com.studentmanagement.attendance.model.SubjectAttendance;
import com.studentmanagement.attendance.repository.AttendanceRepository;
import com.studentmanagement.attendance.repository.SubjectAttendanceRepository;
import com.studentmanagement.attendance.service.AttendanceService;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.AttendanceStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.security.SecurityUtils;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SubjectAttendanceRepository subjectAttendanceRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRequest request) {
        log.info("Recording attendance for student: {}, date: {}", request.getStudentId(), request.getDate());

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        LocalDate date = request.getDate() != null ? LocalDate.parse(request.getDate()) : LocalDate.now();

        AttendanceStatus status;
        try {
            status = AttendanceStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }

        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User recordedBy = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        Attendance attendance = Attendance.builder()
                .student(student)
                .date(date)
                .status(status)
                .notes(request.getNotes())
                .recordedBy(recordedBy)
                .build();
        attendance = attendanceRepository.save(attendance);

        log.info("Attendance recorded: id={}, student={}, status={}", attendance.getId(), student.getId(), status);
        return toResponse(attendance);
    }

    @Override
    @Transactional
    public List<AttendanceResponse> bulkRecordAttendance(BulkAttendanceRequest request) {
        log.info("Bulk recording attendance, {} records", request.getRecords().size());

        LocalDate date = LocalDate.parse(request.getDate());
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User recordedBy = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        List<AttendanceResponse> responses = new ArrayList<>();
        for (BulkAttendanceRequest.Record record : request.getRecords()) {
            Student student = studentRepository.findById(record.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", record.getStudentId()));

            AttendanceStatus status;
            try {
                status = AttendanceStatus.valueOf(record.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + record.getStatus());
            }

            Attendance attendance = Attendance.builder()
                    .student(student)
                    .date(date)
                    .status(status)
                    .notes(record.getNotes())
                    .recordedBy(recordedBy)
                    .build();
            attendance = attendanceRepository.save(attendance);
            responses.add(toResponse(attendance));
        }

        log.info("Bulk attendance recorded: {} records", responses.size());
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> listAttendance(int page, int size, UUID studentId, UUID sessionId, String date) {
        log.debug("Listing attendance - student: {}, session: {}, date: {}", studentId, sessionId, date);
        List<Attendance> records;

        if (studentId != null) {
            records = attendanceRepository.findByStudentId(studentId);
        } else if (date != null) {
            records = attendanceRepository.findByDate(LocalDate.parse(date));
        } else if (sessionId != null) {
            records = attendanceRepository.findByStudentClassId(sessionId);
        } else {
            records = attendanceRepository.findAll();
        }

        return records.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendance(UUID studentId) {
        log.debug("Getting attendance for student: {}", studentId);
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSummaryResponse> getStudentAttendanceSummary(UUID studentId) {
        log.debug("Getting attendance summary for student: {}", studentId);
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }

        List<SubjectAttendance> subjectRecords = subjectAttendanceRepository.findByStudentId(studentId);
        Map<UUID, List<SubjectAttendance>> byCourse = subjectRecords.stream()
                .filter(sa -> sa.getCourse() != null)
                .collect(Collectors.groupingBy(sa -> sa.getCourse().getId()));

        List<AttendanceSummaryResponse> summaries = new ArrayList<>();
        for (Map.Entry<UUID, List<SubjectAttendance>> entry : byCourse.entrySet()) {
            List<SubjectAttendance> records = entry.getValue();
            long total = records.size();
            long attended = records.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count();
            long absent = records.stream().filter(r -> r.getStatus() == AttendanceStatus.ABSENT).count();
            long late = records.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count();
            double percentage = total > 0 ? (double) attended / total * 100.0 : 0.0;

            summaries.add(AttendanceSummaryResponse.builder()
                    .subjectName(records.get(0).getCourse().getName())
                    .totalSessions((int) total)
                    .attended((int) attended)
                    .absent((int) absent)
                    .late((int) late)
                    .percentage(Math.round(percentage * 100.0) / 100.0)
                    .build());
        }

        return summaries;
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(UUID id, AttendanceRequest request) {
        log.info("Updating attendance: {}", id);
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));

        AttendanceStatus status;
        try {
            status = AttendanceStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }

        attendance.setStatus(status);
        if (request.getDate() != null) attendance.setDate(LocalDate.parse(request.getDate()));
        if (request.getNotes() != null) attendance.setNotes(request.getNotes());
        attendance = attendanceRepository.save(attendance);

        log.info("Attendance updated: {}", id);
        return toResponse(attendance);
    }

    private AttendanceResponse toResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .sessionId(attendance.getStudentClass() != null ? attendance.getStudentClass().getId() : null)
                .date(attendance.getDate().toString())
                .status(attendance.getStatus().name())
                .notes(attendance.getNotes())
                .build();
    }
}
