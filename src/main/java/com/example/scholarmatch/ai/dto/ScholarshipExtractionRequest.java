package com.example.scholarmatch.ai.dto;

import jakarta.validation.constraints.NotBlank;

public class ScholarshipExtractionRequest {

    @NotBlank
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}