package com.studentmanagement.auth.controller;

import com.studentmanagement.auth.dto.*;
import com.studentmanagement.auth.service.AuthService;
import com.studentmanagement.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh() {
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return null;
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<ApiResponse<Void>> setupMfa() {
        return null;
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse<Void>> verifyMfa(@Valid @RequestBody MfaRequest request) {
        return null;
    }

    @PostMapping("/mfa/validate")
    public ResponseEntity<ApiResponse<Void>> validateMfa(@Valid @RequestBody MfaRequest request) {
        return null;
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return null;
    }

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return null;
    }

    @PutMapping("/password/change")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return null;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        return null;
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return null;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        return null;
    }

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRolesRequest request) {
        return null;
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return null;
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<java.util.List<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return null;
    }
}
