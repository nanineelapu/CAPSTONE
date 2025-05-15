package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.service.EmailService;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void testSendConfirmationEmailSuccess() {
        String to = "test@example.com";
        String mobileNumber = "1234567890";
        String planName = "Test Plan";
        double amount = 100.0;
        String transactionId = "test-transaction-id";

        emailService.sendConfirmationEmail(to, mobileNumber, planName, amount, transactionId);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendConfirmationEmailFailure() {
        String to = "test@example.com";
        String mobileNumber = "1234567890";
        String planName = "Test Plan";
        double amount = 100.0;
        String transactionId = "test-transaction-id";

        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(SimpleMailMessage.class));

        try {
            emailService.sendConfirmationEmail(to, mobileNumber, planName, amount, transactionId);
        } catch (Exception e) {
            // Exception caught as expected
        }

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}