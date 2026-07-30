package com.studentmanagement.academic.service;

import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.model.Result;
import com.studentmanagement.academic.model.Transcript;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.ResultRepository;
import com.studentmanagement.academic.repository.TranscriptRepository;
import com.studentmanagement.common.enums.GradeStatus;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final ResultRepository resultRepository;

    @Transactional(readOnly = true)
    public byte[] generateTranscript(UUID studentId) {
        log.info("Generating transcript for student: {}", studentId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        List<Grade> grades = gradeRepository.findByStudentId(studentId).stream()
                .filter(g -> g.getStatus() == GradeStatus.PUBLISHED)
                .toList();

        List<Result> results = resultRepository.findByStudentId(studentId);

        StringBuilder sb = new StringBuilder();
        sb.append("STUDENT TRANSCRIPT\n");
        sb.append("==================\n\n");
        sb.append("Name: ").append(student.getUser() != null ? student.getUser().getFullName() : student.getFirstName() + " " + student.getLastName()).append("\n");
        sb.append("Student No: ").append(student.getStudentNo()).append("\n");
        sb.append("Programme: ").append(student.getProgramme() != null ? student.getProgramme().getName() : "N/A").append("\n\n");

        sb.append("--- Grades ---\n");
        for (Grade grade : grades) {
            sb.append(String.format("  %s: %s (Score: %.2f, Grade: %s)%n",
                    grade.getCourse() != null ? grade.getCourse().getCode() : "N/A",
                    grade.getCourse() != null ? grade.getCourse().getName() : "N/A",
                    grade.getScore() != null ? grade.getScore().doubleValue() : 0.0,
                    grade.getLetterGrade() != null ? grade.getLetterGrade() : "N/A"));
        }

        sb.append("\n--- Semester Results ---\n");
        for (Result result : results) {
            sb.append(String.format("  %s: GPA=%.2f, Credits=%d%n",
                    result.getSemester() != null ? result.getSemester().getName() : "N/A",
                    result.getGpa() != null ? result.getGpa().doubleValue() : 0.0,
                    result.getTotalCredits()));
        }

        sb.append("\nGenerated: ").append(Instant.now()).append("\n");

        Transcript transcript = Transcript.builder()
                .student(student)
                .generatedAt(Instant.now())
                .build();
        transcriptRepository.save(transcript);

        log.info("Transcript generated for student: {}", studentId);
        return sb.toString().getBytes();
    }

}
