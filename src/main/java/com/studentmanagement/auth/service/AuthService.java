package com.studentmanagement.auth.service;

import com.studentmanagement.auth.dto.AuthResponse;
import com.studentmanagement.auth.dto.LoginRequest;
import com.studentmanagement.auth.dto.UserResponse;
import com.studentmanagement.auth.model.User;
import com.studentmanagement.auth.model.VerificationToken;
import com.studentmanagement.auth.repository.UserRepository;
import com.studentmanagement.auth.repository.VerificationTokenRepository;
import com.studentmanagement.common.exception.BadRequestException;
import com.studentmanagement.common.exception.ResourceNotFoundException;
import com.studentmanagement.common.exception.UnauthorizedException;
import com.studentmanagement.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (user.getStatus().name().equals("PENDING")) {
            throw new UnauthorizedException("ACCOUNT_NOT_ACTIVATED",
                    "Your account has not been activated yet. Please wait for admin approval.");
        }

        if (!user.getStatus().name().equals("ACTIVE")) {
            throw new UnauthorizedException("ACCOUNT_DISABLED",
                    "Your account is " + user.getStatus().name().toLowerCase() + ". Contact administration.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .mfaEnabled(user.getMfaEnabled())
                    .build();

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtProvider.getExpirationMs() / 1000)
                    .user(userResponse)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Invalid verification token"));

        if (verificationToken.getUsed()) {
            throw new BadRequestException("Token has already been used");
        }

        if (verificationToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Verification token has expired");
        }

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(Instant.now());
        userRepository.save(user);
    }

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
}
