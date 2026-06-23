package com.studentmanagement.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public static UUID getCurrentUserId() {
        UserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getId() : null;
    }

    public static String getCurrentUserEmail() {
        UserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getEmail() : null;
    }

    public static String getCurrentUserRole() {
        UserPrincipal principal = getCurrentUser();
        return principal != null ? principal.getRole() : null;
    }

    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}
