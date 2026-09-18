package com.example.scholarmatch.certificateportallink.model;

import java.time.LocalDate;

public class CertificatePortalLink {

    private Long portalLinkId;
    private Long certificateTypeId;
    private String state;
    private String portalName;
    private String portalUrl;
    private LocalDate lastVerifiedDate;

    public CertificatePortalLink() {
    }

    public Long getPortalLinkId() {
        return portalLinkId;
    }

    public void setPortalLinkId(Long portalLinkId) {
        this.portalLinkId = portalLinkId;
    }

    public Long getCertificateTypeId() {
        return certificateTypeId;
    }

    public void setCertificateTypeId(Long certificateTypeId) {
        this.certificateTypeId = certificateTypeId;
    }

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