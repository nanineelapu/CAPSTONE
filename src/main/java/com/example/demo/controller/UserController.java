package com.example.demo.controller;

import com.example.demo.config.RateLimiter;
import com.example.demo.dto.UserLoginRequest;
import com.example.demo.dto.UserLoginResponse;
import com.example.demo.dto.UserRegistrationRequest;
import com.example.demo.dto.MobileValidationRequest;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
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
@RequestMapping("/api")
@CrossOrigin(origins = {"https://main.d2l6bpupzeebpz.amplifyapp.com","http://localhost:4200"})
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RateLimiter rateLimiter;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // Validate input
        if (request.getMobileNumber() == null || !request.getMobileNumber().matches("^[0-9]{10}$")) {
            return ResponseEntity.badRequest().body("Invalid mobile number");
        }
        
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }
        
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            return ResponseEntity.badRequest().body("Mobile number already registered");
        }
        
        // Create new user
        User user = new User();
        user.setMobileNumber(request.getMobileNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        // Validate input
        if (request.getMobileNumber() == null || request.getPassword() == null) {

            return ResponseEntity.badRequest().body("Email and password are required");
        }
        
        // Apply rate limiting
        if (!rateLimiter.allowRequest(request.getMobileNumber().toString())) {
            return ResponseEntity.status(429).body("Too many requests, please try again later");
        }
        
        // Find user and validate password
        User user = userRepository.findByEmail(request.getMobileNumber().toString());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        
        try {
            // Generate JWT token
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            Key key = Keys.hmacShaKeyFor(keyBytes);
            
            String token = Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("mobileNumber", user.getMobileNumber())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(key)
                    .compact();
            
            return ResponseEntity.ok(new UserLoginResponse(token, user.getMobileNumber()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating token");
        }
    }

    @PostMapping("/user/validate-user")
    public ResponseEntity<?> validateMobile(@RequestBody MobileValidationRequest request) {
        if (request.getMobileNumber() == null || !request.getMobileNumber().matches("^[0-9]{10}$")) {
            return ResponseEntity.badRequest().body("Invalid mobile number");
        }
        
        User user = userRepository.findByMobileNumber(request.getMobileNumber());
        if (user == null) {
            return ResponseEntity.status(404).body("Mobile number not found");
        }
        
        return ResponseEntity.ok("Mobile number validated successfully");
    }
}