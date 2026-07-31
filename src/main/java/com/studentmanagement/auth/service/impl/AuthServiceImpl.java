package com.studentmanagement.auth.service.impl;

import com.studentmanagement.auth.dto.*;
import com.studentmanagement.auth.model.RefreshToken;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.model.VerificationToken;
import com.studentmanagement.auth.repository.RefreshTokenRepository;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.auth.repository.VerificationTokenRepository;
import com.studentmanagement.auth.service.AuthService;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.exception.UnauthorizedException;
import com.studentmanagement.common.security.JwtProvider;
import com.studentmanagement.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed: no user found for email: {}", request.getEmail());
                    return new BadRequestException("Invalid email or password");
                });

        if (user.getStatus().name().equals("PENDING")) {
            log.warn("Login failed: account not activated for email: {}", request.getEmail());
            throw new UnauthorizedException("ACCOUNT_NOT_ACTIVATED",
                    "Your account has not been activated yet. Please wait for admin approval.");
        }

        if (!user.getStatus().name().equals("ACTIVE")) {
            log.warn("Login failed: account {} for email: {}", user.getStatus().name().toLowerCase(), request.getEmail());
            throw new UnauthorizedException("ACCOUNT_DISABLED",
                    "Your account is " + user.getStatus().name().toLowerCase() + ". Contact administration.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
            String refreshTokenStr = createRefreshToken(user);

            UserResponse userResponse = buildUserResponse(user);

            log.info("Login successful for email: {} (user: {})", request.getEmail(), user.getId());
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshTokenStr)
                    .tokenType("Bearer")
                    .expiresIn(jwtProvider.getExpirationMs() / 1000)
                    .user(userResponse)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("Login failed: invalid password for email: {}", request.getEmail());
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.debug("Refreshing token");
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> {
                    log.warn("Refresh failed: invalid token");
                    return new BadRequestException("Invalid refresh token");
                });

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            log.warn("Refresh failed: expired token");
            throw new BadRequestException("Refresh token has expired");
        }

        User user = refreshToken.getUser();
        refreshTokenRepository.delete(refreshToken);

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRefreshToken = createRefreshToken(user);

        log.info("Token refreshed for user: {}", user.getId());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getExpirationMs() / 1000)
                .user(buildUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        log.debug("Logging out");
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshToken -> {
                    refreshTokenRepository.delete(refreshToken);
                    log.info("User logged out: {}", refreshToken.getUser().getId());
                });
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token");
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Email verification failed: invalid token");
                    return new BadRequestException("Invalid verification token");
                });

        if (verificationToken.getUsed()) {
            log.warn("Email verification failed: token already used for user: {}", verificationToken.getUser().getId());
            throw new BadRequestException("Token has already been used");
        }

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Email verification failed: token expired for user: {}", verificationToken.getUser().getId());
            throw new BadRequestException("Verification token has expired");
        }

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        log.info("Password forgot requested for email: {}", request.getEmail());
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            VerificationToken token = VerificationToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .type("PASSWORD_RESET")
                    .expiresAt(Instant.now().plusSeconds(3600))
                    .used(false)
                    .build();
            verificationTokenRepository.save(token);
            log.info("Password reset token generated for user: {}", user.getId());
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        log.info("Resetting password with token");
        VerificationToken token = verificationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> {
                    log.warn("Password reset failed: invalid token");
                    return new BadRequestException("Invalid or expired reset token");
                });

        if (!"PASSWORD_RESET".equals(token.getType())) {
            log.warn("Password reset failed: wrong token type: {}", token.getType());
            throw new BadRequestException("Invalid reset token");
        }

        if (token.getUsed()) {
            log.warn("Password reset failed: token already used");
            throw new BadRequestException("Reset token has already been used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            log.warn("Password reset failed: token expired");
            throw new BadRequestException("Reset token has expired");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        token.setUsed(true);
        userRepository.save(user);
        verificationTokenRepository.save(token);
        log.info("Password reset successfully for user: {}", user.getId());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getAuthenticatedUser();
        log.info("Changing password for user: {}", currentUser.getId());

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPasswordHash())) {
            log.warn("Password change failed: incorrect current password");
            throw new BadRequestException("Current password is incorrect");
        }

        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        log.info("Password changed successfully for user: {}", currentUser.getId());
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        log.debug("Returning current user: {}", user.getId());
        return buildUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user: {} {} <{}>", request.getFirstName(), request.getLastName(), request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User creation failed: email already exists: {}", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .fullName(request.getFirstName() + " " + request.getLastName())
                .role(request.getRole() != null ? request.getRole() : "STAFF")
                .status(com.studentmanagement.common.enums.UserStatus.ACTIVE)
                .emailVerified(false)
                .mfaEnabled(false)
                .failedAttempts(0)
                .build();
        user = userRepository.save(user);
        log.info("User created successfully: id={}, role={}", user.getId(), user.getRole());
        return buildUserResponse(user);
    }

    @Override
    public List<UserResponse> listUsers(int page, int size) {
        log.debug("Listing users - page: {}, size: {}", page, size);
        return userRepository.findAll().stream()
                .map(this::buildUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUser(UUID id) {
        log.debug("Fetching user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });
        return buildUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserRoles(UUID id, UpdateRolesRequest request) {
        log.info("Updating roles for user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update roles failed: user not found: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        String newRole = request.getRoles() != null && !request.getRoles().isEmpty()
                ? request.getRoles().iterator().next()
                : "STAFF";
        user.setRole(newRole);
        user = userRepository.save(user);
        log.info("User {} role updated to {}", id, newRole);
        return buildUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID id, UpdateUserStatusRequest request) {
        log.info("Updating status for user: {} to {}", id, request.getStatus());
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update status failed: user not found: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        try {
            com.studentmanagement.common.enums.UserStatus newStatus =
                    com.studentmanagement.common.enums.UserStatus.valueOf(request.getStatus().toUpperCase());
            user.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            log.warn("Update status failed: invalid status: {}", request.getStatus());
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }

        user = userRepository.save(user);
        log.info("User {} status updated to {}", id, user.getStatus());
        return buildUserResponse(user);
    }

    @Override
    public VerificationToken createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .user(user)
                .token(token)
                .type("VERIFY_EMAIL")
                .expiresAt(Instant.now().plusSeconds(86400))
                .used(false)
                .build();
        return verificationTokenRepository.save(verificationToken);
    }

    private String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusSeconds(604800))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    private User getAuthenticatedUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("UNAUTHENTICATED", "You must be logged in");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .mfaEnabled(user.getMfaEnabled())
                .build();
    }
}
