package com.app.userservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(fromAddress); m.setTo(toEmail);
            m.setSubject("Your Verification Code - Quantity Measurement");
            m.setText("Hi,\n\nYour verification code is: " + otp + "\n\nThis code expires in 5 minutes.\n\nRegards,\nQuantity Measurement Team");
            mailSender.send(m);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception ex) { log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage()); }
    }

    @Async
    public void sendRegistrationEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(fromAddress); m.setTo(toEmail);
            m.setSubject("Welcome to Quantity Measurement App!");
            m.setText("Hi " + userName + ",\n\nYour account has been created successfully.\n\nRegards,\nQuantity Measurement Team");
            mailSender.send(m);
        } catch (Exception ex) { log.error("Failed to send registration email to {}: {}", toEmail, ex.getMessage()); }
    }

    @Async
    public void sendLoginNotificationEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(fromAddress); m.setTo(toEmail);
            m.setSubject("New login to your Quantity Measurement account");
            m.setText("Hi,\n\nWe noticed a new login to your account.\n\nIf this was not you, please reset your password.\n\nRegards,\nQuantity Measurement Team");
            mailSender.send(m);
        } catch (Exception ex) { log.error("Failed to send login email to {}: {}", toEmail, ex.getMessage()); }
    }

    @Async
    public void sendForgotPasswordEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(fromAddress); m.setTo(toEmail);
            m.setSubject("Your password has been changed");
            m.setText("Hi,\n\nYour password has been changed successfully.\n\nRegards,\nQuantity Measurement Team");
            mailSender.send(m);
        } catch (Exception ex) { log.error("Failed to send forgot-password email to {}: {}", toEmail, ex.getMessage()); }
    }

    @Async
    public void sendPasswordResetEmail(String toEmail) {
        try {
            SimpleMailMessage m = new SimpleMailMessage();
            m.setFrom(fromAddress); m.setTo(toEmail);
            m.setSubject("Password reset successfully");
            m.setText("Hi,\n\nYour password has been reset successfully.\n\nRegards,\nQuantity Measurement Team");
            mailSender.send(m);
        } catch (Exception ex) { log.error("Failed to send password-reset email to {}: {}", toEmail, ex.getMessage()); }
    }
}
