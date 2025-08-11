package com.recommender.paper_recommender.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = "http://127.0.0.1:5500/reset-password.html?token=" + token;
        String subject = "Reset Your Password - Paper Recommender";
        String body = "Hello,\n\nYou have requested to reset your password. Please click the link below to set a new one. This link will expire in 24 hours.\n\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com"); // Should be the same as your config
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}