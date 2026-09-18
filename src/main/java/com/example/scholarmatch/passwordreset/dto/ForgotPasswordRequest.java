package com.example.scholarmatch.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ForgotPasswordRequest {

    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "STUDENT|INSTITUTION", message = "Role must be STUDENT or INSTITUTION")
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}