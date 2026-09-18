package com.example.scholarmatch.institutionverificationlog.model;

import java.time.LocalDateTime;

public class InstitutionVerificationLog {

    private Long logId;
    private Long institutionId;
    private Long adminId;          // nullable — automated rule-based pre-check entries have no admin
    private String action;         // SUBMITTED, APPROVED, REJECTED, INFO_REQUESTED, SUSPENDED
    private String reason;
    private LocalDateTime actionAt;

    public InstitutionVerificationLog() {
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

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

    public LocalDateTime getActionAt() {
        return actionAt;
    }

    public void setActionAt(LocalDateTime actionAt) {
        this.actionAt = actionAt;
    }
}