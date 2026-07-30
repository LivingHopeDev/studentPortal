package com.studentmanagement.auth.controller;

import com.studentmanagement.auth.dto.*;
import com.studentmanagement.auth.service.AuditService;
import com.studentmanagement.auth.service.AuthService;
import com.studentmanagement.auth.service.MfaService;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MfaService mfaService;
    private final AuditService auditService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        log.info("Login successful for email: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        log.info("Verifying email with token");
        authService.verifyEmail(token);
        log.info("Email verified successfully");
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<ApiResponse<Void>> setupMfa(HttpServletRequest request) {
        log.info("MFA setup requested");
        mfaService.setup(request);
        return ResponseEntity.ok(ApiResponse.success("MFA code sent to your email", null));
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse<Void>> verifyMfa(@Valid @RequestBody MfaRequest mfaRequest, HttpServletRequest request) {
        log.info("MFA verify requested");
        mfaService.verify(mfaRequest.getCode(), request);
        return ResponseEntity.ok(ApiResponse.success("MFA enabled successfully", null));
    }

    @PostMapping("/mfa/validate")
    public ResponseEntity<ApiResponse<Void>> validateMfa(@Valid @RequestBody MfaRequest mfaRequest, HttpServletRequest request) {
        log.info("MFA validate requested");
        mfaService.validate(mfaRequest.getCode(), request);
        return ResponseEntity.ok(ApiResponse.success("MFA validation successful", null));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Password forgot requested for email: {}", request.getEmail());
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("If the email exists, a reset link has been sent", null));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Password reset requested");
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PutMapping("/password/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        log.info("Password change requested");
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create user requested for email: {}", request.getEmail());
        UserResponse response = authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<UserResponse> response = authService.listUsers(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        UserResponse response = authService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRolesRequest request) {
        log.info("Update roles for user: {}", id);
        UserResponse response = authService.updateUserRoles(id, request);
        return ResponseEntity.ok(ApiResponse.success("User roles updated", response));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        log.info("Update status for user: {} to {}", id, request.getStatus());
        UserResponse response = authService.updateUserStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("User status updated", response));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Audit logs requested - page: {}, size: {}", page, size);
        List<AuditLogResponse> logs = auditService.getLogs(page, size);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
