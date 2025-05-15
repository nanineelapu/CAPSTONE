package com.example.demo.controller;

import com.example.demo.dto.RechargeRequest;

import com.example.demo.model.Plan;
import com.example.demo.model.Subscriber;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.RechargeRepository;
import com.example.demo.repository.SubscriberRepository;
import com.example.demo.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RechargeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanRepository planRepository;

    @MockBean
    private SubscriberRepository subscriberRepository;

    @MockBean
    private RechargeRepository rechargeRepository;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private RechargeRequest rechargeRequest;
    private Subscriber subscriber;
    private Plan plan;

    @BeforeEach
    void setUp() {
        rechargeRequest = new RechargeRequest();
        rechargeRequest.setMobileNumber("1234567890");
        rechargeRequest.setPlanId(1L);
        rechargeRequest.setPaymentMethod("Card");
        rechargeRequest.setPaymentDetails("4242-4242-4242-4242|12/34|123");

        subscriber = new Subscriber();
        subscriber.setMobileNumber("1234567890");
        subscriber.setEmail("test@example.com");
        subscriber.setName("Test User");

        plan = new Plan();
        plan.setId(1L);
        plan.setName("Test Plan");
        plan.setPrice(100.0);
        plan.setDataPerDay("2GB/day");
        plan.setValidityDays(28);
    }

    @Test
    void testRechargeSuccess() throws Exception {
        when(subscriberRepository.findByMobileNumber("1234567890")).thenReturn(subscriber);
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(rechargeRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(subscriberRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());

        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").exists());

        verify(emailService, times(1)).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
        verify(rechargeRepository, times(1)).save(any());
        verify(subscriberRepository, times(1)).save(any());
    }

    @Test
    void testRechargeInvalidCardDetails() throws Exception {
        rechargeRequest.setPaymentDetails("1234-5678-9012-3456|13/34|12"); // Invalid expiry and CVV
        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid card details"));

        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
    }

    @Test
    void testRechargeSubscriberNotFound() throws Exception {
        when(subscriberRepository.findByMobileNumber("1234567890")).thenReturn(null);
        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Subscriber not found"));

        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
    }

    @Test
    void testRechargeInvalidMobileNumber() throws Exception {
        rechargeRequest.setMobileNumber("12345"); // Invalid mobile number
        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid mobile number"));

        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
    }

    @Test
    void testRechargeEmailFailure() throws Exception {
        when(subscriberRepository.findByMobileNumber("1234567890")).thenReturn(subscriber);
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(rechargeRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(subscriberRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        doThrow(new RuntimeException("Email failure")).when(emailService).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());

        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").exists());

        verify(emailService, times(1)).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
        verify(rechargeRepository, times(1)).save(any());
        verify(subscriberRepository, times(1)).save(any());
    }

    @Test
    void testRechargeInvalidPlanId() throws Exception {
        when(subscriberRepository.findByMobileNumber("1234567890")).thenReturn(subscriber);
        when(planRepository.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rechargeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid plan ID"));

        verify(emailService, never()).sendConfirmationEmail(anyString(), anyString(), anyString(), anyDouble(), anyString());
    }
}