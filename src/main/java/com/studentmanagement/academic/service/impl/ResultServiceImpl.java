package com.studentmanagement.academic.service.impl;

import com.studentmanagement.academic.dto.GpaResponse;
import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.model.Result;
import com.studentmanagement.academic.model.Semester;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.ResultRepository;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.academic.service.GpaCalculator;
import com.studentmanagement.academic.service.ResultService;
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
public class ResultServiceImpl implements ResultService {

    private final GradeRepository gradeRepository;
    private final ResultRepository resultRepository;
    private final SemesterRepository semesterRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public GpaResponse calculateAndSaveResult(UUID studentId, UUID semesterId) {
        log.info("Calculating and saving result for student: {}, semester: {}", studentId, semesterId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", semesterId));

        List<Grade> grades = gradeRepository.findByStudentIdAndSemesterId(studentId, semesterId).stream()
                .filter(g -> g.getStatus() == GradeStatus.PUBLISHED)
                .toList();

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            if (grade.getLetterGrade() == null || grade.getCourse().getCredits() == null) continue;
            double gradePoint = GpaCalculator.gradeToPoint(grade.getLetterGrade());
            totalPoints += gradePoint * grade.getCourse().getCredits();
            totalCredits += grade.getCourse().getCredits();
        }

        BigDecimal gpa = totalCredits > 0
                ? BigDecimal.valueOf(totalPoints / totalCredits).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Result result = resultRepository.findByStudentIdAndSemesterId(studentId, semesterId)
                .orElse(Result.builder().student(student).semester(semester).build());
        result.setGpa(gpa);
        result.setTotalCredits(totalCredits);
        resultRepository.save(result);

        log.info("Result saved for student {} semester {}: GPA={}, credits={}", studentId, semesterId, gpa, totalCredits);
        return GpaResponse.builder()
                .gpa(gpa.doubleValue())
                .totalCredits((double) totalCredits)
                .totalPoints(totalPoints)
                .build();
    }
}
