package com.example.scholarmatch.adminactivitylog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminActivityLogRequest {

    @NotNull(message = "Admin id is required")
    private Long adminId;

    @NotBlank(message = "Action type is required")
    private String actionType;

    @NotBlank(message = "Target entity is required")
    private String targetEntity;

    @NotNull(message = "Target id is required")
    private Long targetId;

    private String details;

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}