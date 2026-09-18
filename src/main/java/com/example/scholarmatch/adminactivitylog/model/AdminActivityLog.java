package com.example.scholarmatch.adminactivitylog.model;

import java.time.LocalDateTime;

public class AdminActivityLog {

    private Long activityLogId;
    private Long adminId;
    private String actionType;     // e.g. "APPROVE_SCHOLARSHIP", "DEACTIVATE_LISTING"
    private String targetEntity;   // e.g. "scholarship", "institution"
    private Long targetId;
    private String details;
    private LocalDateTime actionAt;

    public AdminActivityLog() {
    }

    public Long getActivityLogId() {
        return activityLogId;
    }

    public void setActivityLogId(Long activityLogId) {
        this.activityLogId = activityLogId;
    }

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

    public LocalDateTime getActionAt() {
        return actionAt;
    }

    public void setActionAt(LocalDateTime actionAt) {
        this.actionAt = actionAt;
    }
}