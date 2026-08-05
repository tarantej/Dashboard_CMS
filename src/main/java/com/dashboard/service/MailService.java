package com.dashboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String email, String resetLink)
    {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("Dashboard CMS Password Reset");

        message.setText(
                "Click the link below to reset your password:\n\n"
                        + resetLink);

        mailSender.send(message);
    }

}