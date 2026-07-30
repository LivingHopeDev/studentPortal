package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.GpaResponse;
import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.model.Result;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.ResultRepository;
import com.studentmanagement.common.enums.GradeStatus;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GpaCalculator {

    private final GradeRepository gradeRepository;
    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public GpaResponse getGpa(UUID studentId) {
        log.debug("Calculating GPA for student: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Grade> publishedGrades = gradeRepository.findByStudentId(studentId).stream()
                .filter(g -> g.getStatus() == GradeStatus.PUBLISHED)
                .toList();

        if (publishedGrades.isEmpty()) {
            return GpaResponse.builder()
                    .gpa(0.0)
                    .totalCredits(0.0)
                    .totalPoints(0.0)
                    .build();
        }

        double totalPoints = 0.0;
        double totalCredits = 0.0;

        for (Grade grade : publishedGrades) {
            if (grade.getLetterGrade() == null || grade.getCourse().getCredits() == null) continue;
            double gradePoint = gradeToPoint(grade.getLetterGrade());
            int credits = grade.getCourse().getCredits();
            totalPoints += gradePoint * credits;
            totalCredits += credits;
        }

        double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
        gpa = BigDecimal.valueOf(gpa).setScale(2, RoundingMode.HALF_UP).doubleValue();

        Result result = resultRepository.findByStudentIdAndSemesterId(studentId, null).orElse(null);
        // Store/update result logic is in ResultService

        log.info("GPA calculated for student {}: {} (credits: {})", studentId, gpa, totalCredits);
        return GpaResponse.builder()
                .gpa(gpa)
                .totalCredits(totalCredits)
                .totalPoints(totalPoints)
                .build();
    }

    public static double gradeToPoint(String letterGrade) {
        return switch (letterGrade.toUpperCase()) {
            case "A" -> 5.0;
            case "B" -> 4.0;
            case "C" -> 3.0;
            case "D" -> 2.0;
            case "E" -> 1.0;
            case "F" -> 0.0;
            default -> 0.0;
        };
    }

}
