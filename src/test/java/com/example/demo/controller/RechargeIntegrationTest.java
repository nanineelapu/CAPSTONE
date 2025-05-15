package com.example.demo.controller;

import com.example.demo.model.Plan;
import com.example.demo.model.Subscriber;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RechargeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @BeforeEach
    void setUp() {
        Subscriber subscriber = new Subscriber();
        subscriber.setMobileNumber("1234567890");
        subscriber.setEmail("test@example.com");
        subscriber.setName("Test User");
        subscriberRepository.save(subscriber);

        Plan plan = new Plan();
        plan.setId(1L);
        plan.setName("Test Plan");
        plan.setPrice(100.0);
        plan.setDataPerDay("2GB/day");
        plan.setValidityDays(28);
        planRepository.save(plan);
    }

    @Test
    void testRechargeFullFlow() throws Exception {
        String rechargeRequest = """
            {
                "mobileNumber": "1234567890",
                "planId": 1,
                "paymentMethod": "Card",
                "paymentDetails": "4242-4242-4242-4242|12/34|123"
            }
        """;

        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rechargeRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").exists());
    }

    @Test
    void testRechargeWithInvalidCardDetails() throws Exception {
        String rechargeRequest = """
            {
                "mobileNumber": "1234567890",
                "planId": 1,
                "paymentMethod": "Card",
                "paymentDetails": "1234-5678-9012-3456|13/34|12"
            }
        """;

        mockMvc.perform(post("/api/user/recharge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rechargeRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Invalid card details"));
    }
}