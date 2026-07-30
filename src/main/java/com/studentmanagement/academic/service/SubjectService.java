package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.SubjectRequest;
import com.studentmanagement.academic.dto.SubjectResponse;
import com.studentmanagement.academic.model.Course;
import com.studentmanagement.academic.repository.CourseRepository;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<SubjectResponse> listSubjects() {
        log.debug("Listing all subjects");
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        log.info("Creating subject: {} ({})", request.getName(), request.getCode());
        if (courseRepository.findByCode(request.getCode()).isPresent()) {
            log.warn("Subject creation failed: code already exists: {}", request.getCode());
            throw new DuplicateResourceException("Subject code already exists: " + request.getCode());
        }

        Course course = Course.builder()
                .name(request.getName())
                .code(request.getCode())
                .credits(request.getCredits() != null ? request.getCredits() : 0)
                .build();
        course = courseRepository.save(course);
        log.info("Subject created: id={}, code={}", course.getId(), course.getCode());
        return toResponse(course);
    }

    @Transactional
    public SubjectResponse updateSubject(UUID id, SubjectRequest request) {
        log.info("Updating subject: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: subject not found: {}", id);
                    return new ResourceNotFoundException("Subject", "id", id);
                });

        course.setName(request.getName());
        course.setCode(request.getCode());
        if (request.getCredits() != null) course.setCredits(request.getCredits());
        course = courseRepository.save(course);
        log.info("Subject updated: {}", id);
        return toResponse(course);
    }

    private SubjectResponse toResponse(Course course) {
        return SubjectResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .code(course.getCode())
                .credits(course.getCredits())
                .build();
    }

}
