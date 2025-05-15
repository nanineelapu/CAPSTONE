package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String mobileNumber, String planName, Double amount, String transactionId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Recharge Confirmation");
        message.setText("Dear Customer,\n\nYour recharge for mobile number " + mobileNumber +
                " with plan " + planName + " amounting to " + amount +
                " has been successful. Transaction ID: " + transactionId + "\n\nThank you!");
        mailSender.send(message);
    }
}