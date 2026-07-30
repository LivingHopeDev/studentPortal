package com.studentmanagement.communication.service.impl;

import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.communication.dto.NotificationTemplateRequest;
import com.studentmanagement.communication.dto.NotificationTemplateResponse;
import com.studentmanagement.communication.model.NotificationTemplate;
import com.studentmanagement.communication.repository.NotificationTemplateRepository;
import com.studentmanagement.communication.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final NotificationTemplateRepository templateRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> list() {
        return templateRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request) {
        NotificationTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NotificationTemplate", "id", id));

        template.setSubject(request.getSubject());
        template.setBody(request.getBody());
        template = templateRepository.save(template);

        log.info("Notification template updated: id={}", id);
        return toResponse(template);
    }

    private NotificationTemplateResponse toResponse(NotificationTemplate template) {
        return NotificationTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .subject(template.getSubject())
                .body(template.getBody())
                .build();
    }
}
