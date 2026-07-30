package com.studentmanagement.scheduling.service;

import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.scheduling.dto.ExamRequest;
import com.studentmanagement.scheduling.dto.ExamResponse;
import com.studentmanagement.scheduling.model.Exam;
import com.studentmanagement.scheduling.model.Venue;
import com.studentmanagement.scheduling.repository.ExamRepository;
import com.studentmanagement.scheduling.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public ExamResponse createExam(ExamRequest request) {
        log.info("Creating exam for subject: {}", request.getSubjectId());

        Course course = courseRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", request.getVenueId()));

        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = LocalTime.parse(request.getEndTime());

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        List<Exam> conflicts = examRepository.findByVenueIdAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                venue.getId(), date, startTime, endTime);
        if (!conflicts.isEmpty()) {
            log.warn("Venue {} is already booked for this time slot", venue.getName());
            throw new BadRequestException("Venue is already booked for this time slot");
        }

        Exam exam = Exam.builder()
                .course(course)
                .venue(venue)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        exam = examRepository.save(exam);
        log.info("Exam created: id={}", exam.getId());
        return toResponse(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> listExams(int page, int size) {
        log.debug("Listing exams");
        return examRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExamResponse updateExam(UUID id, ExamRequest request) {
        log.info("Updating exam: {}", id);
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        if (request.getSubjectId() != null) {
            Course course = courseRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getSubjectId()));
            exam.setCourse(course);
        }
        if (request.getVenueId() != null) {
            Venue venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", request.getVenueId()));
            exam.setVenue(venue);
        }
        if (request.getDate() != null) exam.setDate(LocalDate.parse(request.getDate()));
        if (request.getStartTime() != null) exam.setStartTime(LocalTime.parse(request.getStartTime()));
        if (request.getEndTime() != null) exam.setEndTime(LocalTime.parse(request.getEndTime()));

        exam = examRepository.save(exam);
        log.info("Exam updated: {}", id);
        return toResponse(exam);
    }

    @Transactional
    public void deleteExam(UUID id) {
        log.info("Deleting exam: {}", id);
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        examRepository.delete(exam);
        log.info("Exam deleted: {}", id);
    }

    private ExamResponse toResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .subjectId(exam.getCourse().getId())
                .classId(null)
                .date(exam.getDate().toString())
                .startTime(exam.getStartTime().toString())
                .endTime(exam.getEndTime().toString())
                .venueId(exam.getVenue() != null ? exam.getVenue().getId() : null)
                .subjectName(exam.getCourse().getName())
                .build();
    }

}
