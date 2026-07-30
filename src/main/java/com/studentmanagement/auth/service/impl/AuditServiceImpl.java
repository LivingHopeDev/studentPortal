package com.studentmanagement.auth.service.impl;

import com.studentmanagement.auth.dto.AuditLogResponse;
import com.studentmanagement.auth.model.AuditLog;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.AuditLogRepository;
import com.studentmanagement.auth.service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void log(User user, String action, String resource, String resourceId, String details, HttpServletRequest request) {
        String ipAddress = request != null ? request.getRemoteAddr() : null;
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .resource(resource)
                .resourceId(resourceId)
                .details(details)
                .ipAddress(ipAddress)
                .build();
        auditLogRepository.save(auditLog);
        log.debug("Audit log saved: {} on {} ({})", action, resource, resourceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogs(int page, int size) {
        Page<AuditLog> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        return auditLogs.stream()
                .map(log -> AuditLogResponse.builder()
                        .action(log.getAction())
                        .performedBy(log.getUser().getFullName())
                        .timestamp(new java.sql.Timestamp(log.getCreatedAt().toEpochMilli()).toLocalDateTime())
                        .details(log.getDetails())
                        .build())
                .toList();
    }
}
