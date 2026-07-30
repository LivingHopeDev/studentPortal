package com.studentmanagement.auth.service;

import com.studentmanagement.auth.model.MfaToken;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.repository.MfaTokenRepository;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.exception.UnauthorizedException;
import com.studentmanagement.common.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final UserRepository userRepository;
    private final MfaTokenRepository mfaTokenRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    private static final int CODE_EXPIRY_SECONDS = 300;

    @Transactional
    public void setup(HttpServletRequest request) {
        User user = getAuthenticatedUser();
        log.info("Setting up MFA for user: {}", user.getId());

        String secret = generateSecret();
        user.setMfaSecret(secret);
        userRepository.save(user);

        sendAndStoreCode(user, request);

        log.info("MFA setup initiated for user: {}", user.getId());
    }

    @Transactional
    public void verify(String code, HttpServletRequest request) {
        User user = getAuthenticatedUser();
        log.info("Verifying MFA setup for user: {}", user.getId());

        validateCode(user, code);

        user.setMfaEnabled(true);
        userRepository.save(user);

        auditService.log(user, "MFA_ENABLED", "USER", user.getId().toString(),
                "Multi-factor authentication enabled", request);

        log.info("MFA enabled successfully for user: {}", user.getId());
    }

    @Transactional
    public void validate(String code, HttpServletRequest request) {
        User user = getAuthenticatedUser();
        log.info("Validating MFA code for user: {}", user.getId());

        if (!user.getMfaEnabled()) {
            log.warn("MFA validation failed: MFA not enabled for user: {}", user.getId());
            throw new BadRequestException("MFA is not enabled for this account");
        }

        validateCode(user, code);

        auditService.log(user, "MFA_VALIDATED", "USER", user.getId().toString(),
                "Multi-factor authentication validated", request);

        log.info("MFA validation successful for user: {}", user.getId());
    }

    private void validateCode(User user, String code) {
        MfaToken mfaToken = mfaTokenRepository
                .findTopByUserIdAndUsedFalseOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> {
                    log.warn("MFA failed: no code found for user: {}", user.getId());
                    return new BadRequestException("No MFA code has been sent. Request a new code.");
                });

        if (mfaToken.getExpiresAt().isBefore(Instant.now())) {
            mfaToken.setUsed(true);
            mfaTokenRepository.save(mfaToken);
            log.warn("MFA failed: code expired for user: {}", user.getId());
            throw new BadRequestException("MFA code has expired. Request a new code.");
        }

        if (!mfaToken.getCode().equals(code)) {
            log.warn("MFA failed: invalid code for user: {}", user.getId());
            throw new BadRequestException("Invalid MFA code");
        }

        mfaToken.setUsed(true);
        mfaTokenRepository.save(mfaToken);
    }

    public void sendAndStoreCode(User user, HttpServletRequest request) {
        mfaTokenRepository.deleteByUserId(user.getId());

        String code = generateCode();
        MfaToken mfaToken = MfaToken.builder()
                .user(user)
                .code(code)
                .expiresAt(Instant.now().plusSeconds(CODE_EXPIRY_SECONDS))
                .used(false)
                .build();
        mfaTokenRepository.save(mfaToken);

        emailService.sendMfaCode(user.getEmail(), user.getFullName(), code);
        log.info("MFA code sent to: {}", user.getEmail());
    }

    private String generateSecret() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private User getAuthenticatedUser() {
        UUID userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new UnauthorizedException("UNAUTHENTICATED", "You must be logged in");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

}
