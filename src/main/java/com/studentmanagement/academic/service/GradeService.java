package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.GradePublishRequest;
import com.studentmanagement.academic.dto.GradeRequest;
import com.studentmanagement.academic.dto.GradeResponse;
import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.model.Grade;
import com.studentmanagement.academic.model.Semester;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.academic.repository.GradeRepository;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.GradeStatus;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.security.SecurityUtils;
import com.studentmanagement.student.model.Student;
import com.studentmanagement.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final UserRepository userRepository;

    @Transactional
    public GradeResponse submitGrade(GradeRequest request) {
        log.info("Submitting grade for student: {}, course: {}, period: {}",
                request.getStudentId(), request.getSubjectId(), request.getPeriodId());

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        Course course = courseRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));
        Semester semester = semesterRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getPeriodId()));

        if (gradeRepository.findByStudentIdAndCourseIdAndSemesterId(
                student.getId(), course.getId(), semester.getId()).isPresent()) {
            log.warn("Grade already exists for student {} course {} period {}", student.getId(), course.getId(), semester.getId());
            throw new BadRequestException("Grade already exists for this student in this subject and period");
        }

        String letterGrade = request.getLetterGrade();
        if (letterGrade == null && request.getScore() != null) {
            letterGrade = calculateLetterGrade(request.getScore());
        }

        Grade grade = Grade.builder()
                .student(student)
                .course(course)
                .semester(semester)
                .score(request.getScore() != null ? BigDecimal.valueOf(request.getScore()) : null)
                .letterGrade(letterGrade)
                .remarks(request.getRemarks())
                .status(GradeStatus.DRAFT)
                .build();
        grade = gradeRepository.save(grade);
        log.info("Grade submitted: id={}", grade.getId());
        return toResponse(grade);
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> listGrades(UUID studentId, UUID subjectId, UUID periodId) {
        log.debug("Listing grades - student: {}, subject: {}, period: {}", studentId, subjectId, periodId);
        List<Grade> grades;
        if (studentId != null && periodId != null) {
            grades = gradeRepository.findByStudentIdAndSemesterId(studentId, periodId);
        } else if (studentId != null) {
            grades = gradeRepository.findByStudentId(studentId);
        } else if (subjectId != null) {
            grades = gradeRepository.findByCourseId(subjectId);
        } else if (periodId != null) {
            grades = gradeRepository.findBySemesterId(periodId);
        } else {
            grades = gradeRepository.findAll();
        }
        return grades.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<GradeResponse> getStudentGrades(UUID studentId) {
        log.debug("Getting grades for student: {}", studentId);
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public GradeResponse updateGrade(UUID id, GradeRequest request) {
        log.info("Updating grade: {}", id);
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade", "id", id));

        if (request.getSubjectId() != null) {
            Course course = courseRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));
            grade.setCourse(course);
        }
        if (request.getPeriodId() != null) {
            Semester semester = semesterRepository.findById(request.getPeriodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getPeriodId()));
            grade.setSemester(semester);
        }
        if (request.getScore() != null) {
            grade.setScore(BigDecimal.valueOf(request.getScore()));
            grade.setLetterGrade(calculateLetterGrade(request.getScore()));
        }
        if (request.getLetterGrade() != null) grade.setLetterGrade(request.getLetterGrade());
        if (request.getRemarks() != null) grade.setRemarks(request.getRemarks());

        grade = gradeRepository.save(grade);
        log.info("Grade updated: {}", id);
        return toResponse(grade);
    }

    @Transactional
    public void publishGrades(GradePublishRequest request) {
        log.info("Publishing grades for period: {}, subject: {}", request.getPeriodId(), request.getSubjectId());
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User publishedBy = currentUserId != null ? userRepository.findById(currentUserId).orElse(null) : null;

        List<Grade> grades = gradeRepository.findBySemesterId(request.getPeriodId()).stream()
                .filter(g -> request.getSubjectId() == null || g.getCourse().getId().equals(request.getSubjectId()))
                .toList();

        for (Grade grade : grades) {
            if (grade.getStatus() == GradeStatus.DRAFT) {
                grade.setStatus(GradeStatus.PUBLISHED);
                grade.setPublishedAt(Instant.now());
                grade.setPublishedBy(publishedBy);
                gradeRepository.save(grade);
            }
        }
        log.info("Published {} grades for period: {}, subject: {}", grades.size(), request.getPeriodId(), request.getSubjectId());
    }

    private String calculateLetterGrade(double score) {
        if (score >= 70) return "A";
        if (score >= 60) return "B";
        if (score >= 50) return "C";
        if (score >= 45) return "D";
        if (score >= 40) return "E";
        return "F";
    }

    private GradeResponse toResponse(Grade grade) {
        return GradeResponse.builder()
                .id(grade.getId())
                .studentId(grade.getStudent().getId())
                .subjectId(grade.getCourse().getId())
                .periodId(grade.getSemester().getId())
                .score(grade.getScore() != null ? grade.getScore().doubleValue() : null)
                .letterGrade(grade.getLetterGrade())
                .remarks(grade.getRemarks())
                .build();
    }

}
