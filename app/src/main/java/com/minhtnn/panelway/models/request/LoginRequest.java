package com.minhtnn.panelway.models.request;

public class LoginRequest {
    private String phoneNumber;
    private String password;
    private String role;

    public LoginRequest(String phoneNumber, String password, String role) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}