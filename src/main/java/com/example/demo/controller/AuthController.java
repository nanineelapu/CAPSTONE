package com.example.demo.controller;

import com.example.demo.config.RateLimiter;
import com.example.demo.dto.AdminLoginRequest;
import com.example.demo.dto.AdminLoginResponse;
import com.example.demo.dto.AdminRegisterRequest;
import com.example.demo.dto.MobileValidationRequest;
import com.example.demo.model.Admin;
import com.example.demo.model.Subscriber;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.SubscriberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RateLimiter rateLimiter;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        // Apply rate limiting
        if (!rateLimiter.allowRequest(request.getUsername())) {
            return ResponseEntity.status(429).body("Too many requests, please try again later");
        }

        Admin admin = adminRepository.findByUsername(request.getUsername());
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        try {
            // Decode the Base64-encoded secret
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // Generate JWT
            String token = Jwts.builder()
                    .setSubject(admin.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(key)
                    .compact();

            System.out.println("Generated JWT: " + token);
            return ResponseEntity.ok(new AdminLoginResponse(token));
        } catch (Exception e) {
            System.err.println("Error generating JWT token: " + e.getMessage());
            return ResponseEntity.status(500).body("Error generating token");
        }
    }

    @PostMapping("/admin/register")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminRegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        if (adminRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        adminRepository.save(admin);

        return ResponseEntity.ok("Admin registered successfully");
    }

    @PostMapping("/validate-mobile")
    public ResponseEntity<?> validateMobile(@RequestBody MobileValidationRequest request) {
        if (request.getMobileNumber() == null || !request.getMobileNumber().matches("^[0-9]{10}$")) {
            return ResponseEntity.badRequest().body("Invalid mobile number");
        }
        Subscriber subscriber = subscriberRepository.findByMobileNumber(request.getMobileNumber());
        if (subscriber == null) {
            return ResponseEntity.status(404).body("Mobile number not found");
        }
        return ResponseEntity.ok(subscriber);
    }
}