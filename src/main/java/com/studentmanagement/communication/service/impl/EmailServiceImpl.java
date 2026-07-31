package com.studentmanagement.communication.service.impl;

import com.studentmanagement.communication.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    @Async
    public void sendVerificationEmail(String to, String name, String token) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationUrl", baseUrl + "/api/v1/auth/verify-email?token=" + token);

            String html = templateEngine.process("mail/verification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Verify Your Email - Student Management Portal");
            helper.setFrom(fromAddress);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Verification email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendMfaCode(String to, String name, String code) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("code", code);

            String html = templateEngine.process("mail/mfa-code", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Your MFA Code - Student Management Portal");
            helper.setFrom(fromAddress);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("MFA code sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send MFA code to {}: {}", to, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendCredentialsEmail(String to, String name, String studentNo, String password) {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("studentNo", studentNo);
            context.setVariable("email", to);
            context.setVariable("password", password);

            String html = templateEngine.process("mail/credentials-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Account Activated - Your Login Credentials");
            helper.setFrom(fromAddress);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Credentials email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send credentials email to {}: {}", to, e.getMessage());
        }
    }
}
