package com.example.scholarmatch.institutionverificationlog.dto;

import jakarta.validation.constraints.NotNull;

public class InstitutionDecisionRequest {

    @NotNull(message = "Admin id is required")
    private Long adminId;

    private String reason;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}