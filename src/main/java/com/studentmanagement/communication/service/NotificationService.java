package com.studentmanagement.communication.service;

import com.studentmanagement.communication.dto.NotificationDto;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    List<NotificationDto> getInbox();

    void markAsRead(UUID id);

    void clearAll();
}
