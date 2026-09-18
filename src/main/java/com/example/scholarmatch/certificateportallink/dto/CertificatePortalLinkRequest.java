package com.example.scholarmatch.certificateportallink.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class CertificatePortalLinkRequest {

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Portal name is required")
    private String portalName;

    @NotBlank(message = "Portal URL is required")
    private String portalUrl;

    private LocalDate lastVerifiedDate;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPortalName() {
        return portalName;
    }

    public void setPortalName(String portalName) {
        this.portalName = portalName;
    }

    public String getPortalUrl() {
        return portalUrl;
    }

    public void setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
    }

    public LocalDate getLastVerifiedDate() {
        return lastVerifiedDate;
    }

    public void setLastVerifiedDate(LocalDate lastVerifiedDate) {
        this.lastVerifiedDate = lastVerifiedDate;
    }
}