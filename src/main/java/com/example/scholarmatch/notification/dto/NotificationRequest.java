package com.example.scholarmatch.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class NotificationRequest {

    @NotBlank(message = "Recipient type is required")
    @Pattern(
            regexp = "STUDENT|INSTITUTION|ADMIN",
            message = "Recipient type must be STUDENT, INSTITUTION or ADMIN"
    )
    private String recipientType;

    @NotNull(message = "Recipient id is required")
    private Long recipientId;

    @NotBlank(message = "Notification type is required")
    @Pattern(
            regexp = "DEADLINE_ALERT|CERTIFICATE_EXPIRY|SCHOLARSHIP_APPROVED|SCHOLARSHIP_REJECTED|GENERAL|INSTITUTION_VERIFICATION|SCHOLARSHIP_APPLICATION|INSTITUTION_REGISTRATION|SCHOLARSHIP_SUBMISSION",
            message = "Invalid notification type"
    )
    private String notificationType;

    private Long relatedScholarshipId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Long getRelatedScholarshipId() {
        return relatedScholarshipId;
    }

    public void setRelatedScholarshipId(Long relatedScholarshipId) {
        this.relatedScholarshipId = relatedScholarshipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}