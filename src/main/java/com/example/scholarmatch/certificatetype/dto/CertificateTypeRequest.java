package com.example.scholarmatch.certificatetype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class CertificateTypeRequest {

    @NotBlank(message = "Certificate name is required")
    private String name;

    private String issuingAuthority;

    @PositiveOrZero(message = "Min processing days cannot be negative")
    private Integer minProcessingDays;

    @PositiveOrZero(message = "Max processing days cannot be negative")
    private Integer maxProcessingDays;

    private boolean tatkaalAvailable;
    private String officialApplyLink;
    private java.util.List<String> requiredDocuments;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public Integer getMinProcessingDays() {
        return minProcessingDays;
    }

    public void setMinProcessingDays(Integer minProcessingDays) {
        this.minProcessingDays = minProcessingDays;
    }

    public Integer getMaxProcessingDays() {
        return maxProcessingDays;
    }

    public void setMaxProcessingDays(Integer maxProcessingDays) {
        this.maxProcessingDays = maxProcessingDays;
    }

    public boolean isTatkaalAvailable() {
        return tatkaalAvailable;
    }

    public void setTatkaalAvailable(boolean tatkaalAvailable) {
        this.tatkaalAvailable = tatkaalAvailable;
    }

    public String getOfficialApplyLink() {
        return officialApplyLink;
    }

    public void setOfficialApplyLink(String officialApplyLink) {
        this.officialApplyLink = officialApplyLink;
    }

    public java.util.List<String> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(java.util.List<String> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}