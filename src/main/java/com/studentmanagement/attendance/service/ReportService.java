package com.studentmanagement.attendance.service;

import com.studentmanagement.attendance.dto.AttendanceResponse;
import com.studentmanagement.attendance.model.Attendance;
import com.studentmanagement.attendance.repository.AttendanceRepository;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.model.StudentClass;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service("attendanceReportService")
@RequiredArgsConstructor
public class ReportService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getClassReport(UUID classId) {
        log.debug("Getting attendance report for class: {}", classId);
        return attendanceRepository.findByStudentClassId(classId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public byte[] getStudentReportPdf(UUID studentId) {
        log.info("Generating attendance PDF report for student: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Attendance> records = attendanceRepository.findByStudentId(studentId);
        long total = records.size();
        long present = records.stream().filter(a -> a.getStatus().name().equals("PRESENT")).count();
        long absent = records.stream().filter(a -> a.getStatus().name().equals("ABSENT")).count();
        long late = records.stream().filter(a -> a.getStatus().name().equals("LATE")).count();
        double percentage = total > 0 ? (double) present / total * 100.0 : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("ATTENDANCE REPORT\n");
        sb.append("=================\n\n");
        sb.append("Name: ").append(student.getUser() != null ? student.getUser().getFullName() : student.getFirstName() + " " + student.getLastName()).append("\n");
        sb.append("Student No: ").append(student.getStudentNo()).append("\n\n");
        sb.append("Summary\n");
        sb.append("  Total Sessions: ").append(total).append("\n");
        sb.append("  Present: ").append(present).append("\n");
        sb.append("  Absent: ").append(absent).append("\n");
        sb.append("  Late: ").append(late).append("\n");
        sb.append(String.format("  Attendance Rate: %.2f%%\n\n", percentage));
        sb.append("--- Details ---\n");
        for (Attendance a : records) {
            sb.append(String.format("  %s - %s", a.getDate(), a.getStatus().name()));
            if (a.getNotes() != null) sb.append(" (").append(a.getNotes()).append(")");
            sb.append("\n");
        }
        sb.append("\nGenerated: ").append(Instant.now()).append("\n");

        return sb.toString().getBytes();
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
