package com.studentmanagement.fees.service;

import com.studentmanagement.academic.model.Semester;
import com.studentmanagement.academic.repository.SemesterRepository;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.fees.dto.FeeScheduleRequest;
import com.studentmanagement.fees.dto.FeeScheduleResponse;
import com.studentmanagement.fees.model.FeeSchedule;
import com.studentmanagement.fees.repository.FeeScheduleRepository;
import com.studentmanagement.student.model.Programme;
import com.studentmanagement.student.repository.ProgrammeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeScheduleService {

    private final FeeScheduleRepository feeScheduleRepository;
    private final ProgrammeRepository programmeRepository;
    private final SemesterRepository semesterRepository;

    @Transactional(readOnly = true)
    public List<FeeScheduleResponse> listSchedules() {
        return feeScheduleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public FeeScheduleResponse createSchedule(FeeScheduleRequest request) {
        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Programme", "id", request.getProgrammeId()));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        FeeSchedule schedule = FeeSchedule.builder()
                .programme(programme)
                .semester(semester)
                .components(request.getComponents())
                .totalAmount(BigDecimal.valueOf(request.getTotalAmount()))
                .build();
        schedule = feeScheduleRepository.save(schedule);

        log.info("Fee schedule created: {}", schedule.getId());
        return toResponse(schedule);
    }

    @Transactional
    public FeeScheduleResponse updateSchedule(UUID id, FeeScheduleRequest request) {
        FeeSchedule schedule = feeScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeSchedule", "id", id));

        Programme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException("Programme", "id", request.getProgrammeId()));

        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        schedule.setProgramme(programme);
        schedule.setSemester(semester);
        schedule.setComponents(request.getComponents());
        schedule.setTotalAmount(BigDecimal.valueOf(request.getTotalAmount()));
        schedule = feeScheduleRepository.save(schedule);

        log.info("Fee schedule updated: {}", id);
        return toResponse(schedule);
    }

    private FeeScheduleResponse toResponse(FeeSchedule schedule) {
        return FeeScheduleResponse.builder()
                .id(schedule.getId())
                .programmeId(schedule.getProgramme() != null ? schedule.getProgramme().getId() : null)
                .programmeName(schedule.getProgramme() != null ? schedule.getProgramme().getName() : null)
                .semesterId(schedule.getSemester() != null ? schedule.getSemester().getId() : null)
                .semesterName(schedule.getSemester() != null ? schedule.getSemester().getName() : null)
                .components(schedule.getComponents())
                .totalAmount(schedule.getTotalAmount() != null ? schedule.getTotalAmount().doubleValue() : 0.0)
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
