package com.example.demo.dto;

public class UserLoginRequest {
    private Long mobileNumber;
    private String password;

    // Getters and Setters
    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setEmail(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}