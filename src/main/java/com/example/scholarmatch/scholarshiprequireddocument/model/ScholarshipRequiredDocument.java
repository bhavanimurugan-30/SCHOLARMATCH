package com.example.scholarmatch.scholarshiprequireddocument.model;

public class ScholarshipRequiredDocument {

    private Long scholarshipId;
    private Long certificateTypeId;
    private boolean mandatory;

    public ScholarshipRequiredDocument() {
    }

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(Long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public Long getCertificateTypeId() {
        return certificateTypeId;
    }

    public void setCertificateTypeId(Long certificateTypeId) {
        this.certificateTypeId = certificateTypeId;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}