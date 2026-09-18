package com.example.scholarmatch.auth.dto;

public class LoginResponse {

    private final String token;
    private final String role;
    private final Long id;
    private final String email;
    private final String name;

    public LoginResponse(String token, String role, Long id, String email, String name) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}