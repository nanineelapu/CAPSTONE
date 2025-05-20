package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.SubscriberRequest;
import com.example.demo.model.Plan;
import com.example.demo.model.Recharge;
import com.example.demo.model.Subscriber;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.RechargeRepository;
import com.example.demo.repository.SubscriberRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://main.d2l6bpupzeebpz.amplifyapp.com", "http://localhost:4200"})
public class SubscriberController {
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RechargeRepository rechargeRepository;

    @GetMapping("/admin/subscribers/expiring")
    public ResponseEntity<List<Subscriber>> getExpiringSubscribers() {
        return ResponseEntity.ok(subscriberRepository.findExpiringSubscribers());
    }

    @GetMapping("/admin/subscribers/{mobileNumber}/history")
    public ResponseEntity<List<Recharge>> getRechargeHistory(@PathVariable String mobileNumber) {
        if (!mobileNumber.matches("^[0-9]{10}$")) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(rechargeRepository.findByMobileNumber(mobileNumber));
    }
    @PostMapping("/admin/addsubscriber")
    public ResponseEntity<?> addSubscriber(@RequestBody SubscriberRequest request) {
        Optional<Plan> planOpt = planRepository.findById(request.getCurrentPlanId());
        if (planOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid Plan ID");
        }

        Subscriber subscriber = new Subscriber();
        subscriber.setMobileNumber(request.getMobileNumber());
        subscriber.setName(request.getName());
        subscriber.setEmail(request.getEmail());
        subscriber.setCurrentPlan(planOpt.get());
        subscriber.setCreatedAt(LocalDate.now());
        subscriber.setDataUsed(0.0);
         subscriber.setDataTotal(parseDataAmount(planOpt.get().getDataPerDay()) * planOpt.get().getValidityDays());// assume `getData()` returns total data
        subscriber.setPlanExpiry(LocalDate.now().plusDays(planOpt.get().getValidityDays())); // example logic

        Subscriber saved = subscriberRepository.save(subscriber);
        return ResponseEntity.ok(saved);
    }
    
    private double parseDataAmount(String dataPerDay) {
    if (dataPerDay == null || dataPerDay.isEmpty()) return 0.0;
    try {
        return Double.parseDouble(dataPerDay.replaceAll("[^0-9.]", ""));
    } catch (NumberFormatException e) {
        return 0.0;
    }
}

    
    
}