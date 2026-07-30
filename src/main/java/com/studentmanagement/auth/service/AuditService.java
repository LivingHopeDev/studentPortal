package com.studentmanagement.auth.service;

import com.studentmanagement.auth.dto.AuditLogResponse;
import com.studentmanagement.auth.model.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AuditService {

    void log(User user, String action, String resource, String resourceId, String details, HttpServletRequest request);

    List<AuditLogResponse> getLogs(int page, int size);
}
