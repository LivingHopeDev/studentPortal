package com.studentmanagement.communication.service;

public interface EmailService {

    void sendVerificationEmail(String to, String name, String token);

    void sendMfaCode(String to, String name, String code);

    void sendCredentialsEmail(String to, String name, String studentNo, String password);
}
