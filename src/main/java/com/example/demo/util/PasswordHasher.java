package com.example.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("Admin@123"));
    }
}