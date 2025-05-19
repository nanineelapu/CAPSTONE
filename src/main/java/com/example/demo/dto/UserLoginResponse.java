package com.example.demo.dto;

public class UserLoginResponse {
    private String token;
    private String mobileNumber;

    public UserLoginResponse(String token, String mobileNumber) {
        this.token = token;
        this.mobileNumber = mobileNumber;
    }

    public String getToken() {
        return token;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
}