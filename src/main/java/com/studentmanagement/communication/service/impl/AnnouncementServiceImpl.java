package com.studentmanagement.communication.service.impl;

import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.enums.MessageAudience;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.security.SecurityUtils;
import com.studentmanagement.communication.dto.AnnouncementRequest;
import com.studentmanagement.communication.dto.AnnouncementResponse;
import com.studentmanagement.communication.model.Announcement;
import com.studentmanagement.communication.repository.AnnouncementRepository;
import com.studentmanagement.communication.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        MessageAudience audience;
        try {
            audience = request.getAudience() != null
                    ? MessageAudience.valueOf(request.getAudience().toUpperCase())
                    : MessageAudience.ALL;
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid audience: " + request.getAudience());
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .body(request.getContent())
                .audience(audience)
                .author(author)
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .publishedAt(Instant.now())
                .build();
        announcement = announcementRepository.save(announcement);

        log.info("Announcement created: id={}, title={}", announcement.getId(), announcement.getTitle());
        return toResponse(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> list() {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse get(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        return toResponse(announcement);
    }

    @Override
    @Transactional
    public AnnouncementResponse update(UUID id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));

        MessageAudience audience;
        try {
            audience = request.getAudience() != null
                    ? MessageAudience.valueOf(request.getAudience().toUpperCase())
                    : announcement.getAudience();
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid audience: " + request.getAudience());
        }

        announcement.setTitle(request.getTitle());
        announcement.setBody(request.getContent());
        announcement.setAudience(audience);
        if (request.getPriority() != null) {
            announcement.setPriority(request.getPriority());
        }
        announcement = announcementRepository.save(announcement);

        log.info("Announcement updated: id={}", id);
        return toResponse(announcement);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Announcement", "id", id));
        announcementRepository.delete(announcement);
        log.info("Announcement deleted: id={}", id);
    }

    private AnnouncementResponse toResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getBody())
                .audience(announcement.getAudience().name())
                .priority(announcement.getPriority())
                .createdBy(announcement.getAuthor().getFirstName() + " " + announcement.getAuthor().getLastName())
                .createdAt(announcement.getCreatedAt() != null
                        ? LocalDateTime.ofInstant(announcement.getCreatedAt(), ZoneId.systemDefault())
                        : null)
                .build();
    }
}
