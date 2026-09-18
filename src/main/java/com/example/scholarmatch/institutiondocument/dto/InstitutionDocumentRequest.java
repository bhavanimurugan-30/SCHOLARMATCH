package com.example.scholarmatch.institutiondocument.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class InstitutionDocumentRequest {

    @NotBlank(message = "Document type is required")
    @Pattern(regexp = "REGISTRATION_CERTIFICATE|PAN|AUTHORIZATION_LETTER|GST_CERTIFICATE",
            message = "Invalid document type")
    private String documentType;

    @NotBlank(message = "File path is required")
    private String filePath;

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
}