package com.studentmanagement.student.service.impl;

import com.studentmanagement.academic.dto.ProgrammeRequest;
import com.studentmanagement.academic.dto.ProgrammeResponse;
import com.studentmanagement.common.exception.DuplicateResourceException;
import com.studentmanagement.student.model.Programme;
import com.studentmanagement.student.repository.ProgrammeRepository;
import com.studentmanagement.student.service.ProgrammeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgrammeServiceImpl implements ProgrammeService {

    private final ProgrammeRepository programmeRepository;

    @Override
    public ProgrammeResponse updateProgramme(UUID id, ProgrammeRequest request) {
        log.info("Updating programme: {}", id);
        Programme programme = programmeRepository.findById(id)
                .orElseThrow(() -> new com.studentmanagement.common.exception.ResourceNotFoundException("Programme", "id", id));

        if (request.getName() != null) programme.setName(request.getName());
        if (request.getCode() != null) programme.setCode(request.getCode());
        if (request.getDurationYears() != null) programme.setDurationYears(request.getDurationYears());
        if (request.getDescription() != null) programme.setDescription(request.getDescription());
        programme = programmeRepository.save(programme);
        log.info("Programme updated: {}", id);
        return toResponse(programme);
    }

    @Override
    public List<ProgrammeResponse> listProgrammes() {
        log.debug("Listing all programmes");
        return programmeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProgrammeResponse createProgramme(ProgrammeRequest request) {
        log.info("Creating programme: {} ({})", request.getName(), request.getCode());
        if (programmeRepository.findByCode(request.getCode()).isPresent()) {
            log.warn("Programme creation failed: code already exists: {}", request.getCode());
            throw new DuplicateResourceException("Programme code already exists: " + request.getCode());
        }
        Programme programme = Programme.builder()
                .name(request.getName())
                .code(request.getCode())
                .durationYears(request.getDurationYears())
                .description(request.getDescription())
                .build();
        programme = programmeRepository.save(programme);
        log.info("Programme created successfully: id={}, code={}", programme.getId(), programme.getCode());
        return toResponse(programme);
    }

    private ProgrammeResponse toResponse(Programme programme) {
        return ProgrammeResponse.builder()
                .id(programme.getId())
                .name(programme.getName())
                .code(programme.getCode())
                .durationYears(programme.getDurationYears())
                .description(programme.getDescription())
                .createdAt(programme.getCreatedAt())
                .updatedAt(programme.getUpdatedAt())
                .build();
    }
}
