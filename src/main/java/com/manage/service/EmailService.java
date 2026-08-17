package com.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);

        message.setSubject("Employee Management System - Password Reset OTP");

        message.setText(
                "Hello,\n\n"
              + "Your OTP for password reset is: " + otp + "\n\n"
              + "This OTP is valid for 5 minutes.\n\n"
              + "If you did not request this password reset, please ignore this email.\n\n"
              + "Regards,\n"
              + "Employee Management System"
        );

        mailSender.send(message);
    }
}