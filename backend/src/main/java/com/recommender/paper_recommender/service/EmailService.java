package com.recommender.paper_recommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // <-- Import this
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}") // <-- Inject the configured username
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://127.0.0.1:5500/reset-password.html?token=" + token; // Note: We should update this later for production
        String subject = "Reset Your Password - Paper Recommender";
        String body = "Hello,\n\nYou have requested to reset your password. Please click the link below to set a new one. This link will expire in 24 hours.\n\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // <-- Use the injected email address
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}