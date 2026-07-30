package com.studentmanagement.communication.service;

import com.studentmanagement.communication.dto.NotificationTemplateRequest;
import com.studentmanagement.communication.dto.NotificationTemplateResponse;

import java.util.List;
import java.util.UUID;

public interface TemplateService {

    List<NotificationTemplateResponse> list();

    NotificationTemplateResponse update(UUID id, NotificationTemplateRequest request);
}
