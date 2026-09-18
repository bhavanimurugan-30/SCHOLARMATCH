package com.example.scholarmatch.institutionverificationlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class InstitutionVerificationLogRequest {

    @NotNull(message = "Admin id is required")
    private Long adminId;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "APPROVED|REJECTED|INFO_REQUESTED|SUSPENDED", message = "Invalid action")
    private String action;

    private String reason;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}