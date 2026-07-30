package com.studentmanagement.communication.service.impl;

import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.security.SecurityUtils;
import com.studentmanagement.communication.dto.NotificationDto;
import com.studentmanagement.communication.model.Notification;
import com.studentmanagement.communication.repository.NotificationRepository;
import com.studentmanagement.communication.service.NotificationService;
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
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getInbox() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));

        UUID currentUserId = SecurityUtils.getCurrentUserId();
        if (!notification.getUser().getId().equals(currentUserId)) {
            log.warn("User {} attempted to mark notification {} as read but is not the owner", currentUserId, id);
            return;
        }

        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);

        log.info("Notification marked as read: id={}", id);
    }

    @Override
    @Transactional
    public void clearAll() {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        notificationRepository.deleteByUserId(currentUserId);
        log.info("All notifications cleared for user: {}", currentUserId);
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getBody())
                .type(notification.getType().name())
                .read(notification.getIsRead())
                .createdAt(notification.getCreatedAt() != null
                        ? LocalDateTime.ofInstant(notification.getCreatedAt(), ZoneId.systemDefault())
                        : null)
                .build();
    }
}
