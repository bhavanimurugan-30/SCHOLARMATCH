package com.example.scholarmatch.certificatetype.model;

public class CertificateType {

    private Long certificateTypeId;
    private String name;
    private String issuingAuthority;
    private Integer minProcessingDays;
    private Integer maxProcessingDays;
    private boolean tatkaalAvailable;
    private String officialApplyLink;
    private java.util.List<String> requiredDocuments;

    public CertificateType() {
    }

    public Long getCertificateTypeId() {
        return certificateTypeId;
    }

    public void setCertificateTypeId(Long certificateTypeId) {
        this.certificateTypeId = certificateTypeId;
    }

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