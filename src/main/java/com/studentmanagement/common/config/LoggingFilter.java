package com.studentmanagement.common.config;

import com.studentmanagement.common.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        MDC.put("request_id", requestId);

        var user = SecurityUtils.getCurrentUser();
        if (user != null) {
            MDC.put("user_id", user.getId().toString());
            MDC.put("user_email", user.getEmail());
        }

        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String path = query != null ? uri + "?" + query : uri;

        log.info("--> {} {}", method, path);

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unhandled exception in request: {} {}", method, path, e);
            throw new RuntimeException(e);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();
            log.info("<-- {} {} -> {} ({}ms)", method, path, status, duration);
            MDC.clear();
        }
    }
}
