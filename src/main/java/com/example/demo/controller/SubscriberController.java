package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Recharge;
import com.example.demo.model.Subscriber;
import com.example.demo.repository.RechargeRepository;
import com.example.demo.repository.SubscriberRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class SubscriberController {
    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private RechargeRepository rechargeRepository;

    @GetMapping("/subscribers/expiring")
    public ResponseEntity<List<Subscriber>> getExpiringSubscribers() {
        return ResponseEntity.ok(subscriberRepository.findExpiringSubscribers());
    }

    @GetMapping("/subscribers/{mobileNumber}/history")
    public ResponseEntity<List<Recharge>> getRechargeHistory(@PathVariable String mobileNumber) {
        if (!mobileNumber.matches("^[0-9]{10}$")) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(rechargeRepository.findByMobileNumber(mobileNumber));
    }
}