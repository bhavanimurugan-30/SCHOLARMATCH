package com.example.scholarmatch.scholarshiprequireddocument.dto;

import jakarta.validation.constraints.NotNull;

public class ScholarshipRequiredDocumentRequest {

    @NotNull(message = "Certificate type id is required")
    private Long certificateTypeId;

    private boolean mandatory = true;

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