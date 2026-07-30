package com.studentmanagement.auth.service;

import com.studentmanagement.auth.dto.*;
import com.studentmanagement.auth.model.User;

import java.util.List;
import java.util.UUID;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);

    void verifyEmail(String token);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    UserResponse getCurrentUser();

    UserResponse createUser(CreateUserRequest request);

    List<UserResponse> listUsers(int page, int size);

    UserResponse getUser(UUID id);

    UserResponse updateUserRoles(UUID id, UpdateRolesRequest request);

    UserResponse updateUserStatus(UUID id, UpdateUserStatusRequest request);

    VerificationToken createVerificationToken(User user);
}
