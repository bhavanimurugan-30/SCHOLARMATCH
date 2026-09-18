package com.example.scholarmatch.institutiondocument.model;

import java.time.LocalDateTime;

public class InstitutionDocument {

    private Long institutionDocumentId;
    private Long institutionId;
    private String documentType;   // REGISTRATION_CERTIFICATE, PAN, AUTHORIZATION_LETTER, GST_CERTIFICATE
    private String filePath;
    private Double aiPrecheckConfidence;
    private LocalDateTime uploadedAt;

    public InstitutionDocument() {
    }

    public Long getInstitutionDocumentId() {
        return institutionDocumentId;
    }

    public void setInstitutionDocumentId(Long institutionDocumentId) {
        this.institutionDocumentId = institutionDocumentId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Double getAiPrecheckConfidence() {
        return aiPrecheckConfidence;
    }

    public void setAiPrecheckConfidence(Double aiPrecheckConfidence) {
        this.aiPrecheckConfidence = aiPrecheckConfidence;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}