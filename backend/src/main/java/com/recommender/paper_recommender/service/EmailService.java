package com.recommender.paper_recommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.base-url}") // <-- Inject the frontend URL
    private String frontendBaseUrl;

    public void sendPasswordResetEmail(String to, String token) {
        // Use the configured frontend URL to build the link
        String resetUrl = frontendBaseUrl + "/reset-password.html?token=" + token;
        String subject = "Reset Your Password - Paper Recommender";
        String body = "Hello,\n\nYou have requested to reset your password. Please click the link below to set a new one. This link will expire in 24 hours.\n\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}