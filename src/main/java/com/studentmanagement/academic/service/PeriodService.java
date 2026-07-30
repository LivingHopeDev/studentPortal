package com.studentmanagement.academic.service;

import com.studentmanagement.academic.dto.PeriodRequest;
import com.studentmanagement.academic.dto.PeriodResponse;
import com.studentmanagement.academic.model.Semester;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.common.enums.AcademicPeriodType;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeriodService {

    private final SemesterRepository semesterRepository;

    @Transactional(readOnly = true)
    public List<PeriodResponse> listPeriods() {
        log.debug("Listing all periods");
        return semesterRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PeriodResponse createPeriod(PeriodRequest request) {
        log.info("Creating period: {}", request.getName());
        if (semesterRepository.findByCode(request.getName().toUpperCase().replace(" ", "_")).isPresent()) {
            throw new DuplicateResourceException("Period already exists with name: " + request.getName());
        }

        Semester semester = Semester.builder()
                .name(request.getName())
                .code(request.getName().toUpperCase().replace(" ", "_"))
                .startDate(LocalDate.parse(request.getStartDate()))
                .endDate(LocalDate.parse(request.getEndDate()))
                .isCurrent(false)
                .type(AcademicPeriodType.SEMESTER)
                .build();
        semester = semesterRepository.save(semester);
        log.info("Period created: id={}", semester.getId());
        return toResponse(semester);
    }

    @Transactional
    public PeriodResponse updatePeriod(UUID id, PeriodRequest request) {
        log.info("Updating period: {}", id);
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: period not found: {}", id);
                    return new ResourceNotFoundException("Period", "id", id);
                });

        semester.setName(request.getName());
        semester.setStartDate(LocalDate.parse(request.getStartDate()));
        semester.setEndDate(LocalDate.parse(request.getEndDate()));
        semester = semesterRepository.save(semester);
        log.info("Period updated: {}", id);
        return toResponse(semester);
    }

    @Transactional
    public PeriodResponse setCurrentPeriod(UUID id) {
        log.info("Setting current period: {}", id);
        semesterRepository.findByIsCurrentTrue().ifPresent(s -> {
            s.setIsCurrent(false);
            semesterRepository.save(s);
        });

        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Set current failed: period not found: {}", id);
                    return new ResourceNotFoundException("Period", "id", id);
                });

        semester.setIsCurrent(true);
        semester = semesterRepository.save(semester);
        log.info("Current period set to: {}", id);
        return toResponse(semester);
    }

    private PeriodResponse toResponse(Semester semester) {
        return PeriodResponse.builder()
                .id(semester.getId())
                .name(semester.getName())
                .startDate(semester.getStartDate().toString())
                .endDate(semester.getEndDate().toString())
                .current(semester.getIsCurrent())
                .build();
    }

}
