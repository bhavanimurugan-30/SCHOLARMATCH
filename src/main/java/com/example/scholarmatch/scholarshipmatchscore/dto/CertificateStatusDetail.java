package com.example.scholarmatch.scholarshipmatchscore.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Per-certificate status for one scholarship's document requirements.
 *
 * "Get Certificate" guidance rules (no invented links anywhere):
 *  - GOVERNMENT      -> applyLinks are taken ONLY from certificate_type.official_apply_link
 *                       and certificate_portal_link.portal_url rows that exist in the DB.
 *                       If the DB has no link, applyLinks stays empty and only guidance text is shown.
 *  - ISSUER_PROVIDED -> Marksheet / Bonafide / Bank Passbook / TC etc. No links at all,
 *                       only "Get it from your College/School/Bank" guidance.
 *  - PROFILE         -> Profile Photo. Handled inside the app itself.
 */
public class CertificateStatusDetail {

    private Long certificateTypeId;
    private String certificateName;
    private boolean mandatory;

    /** VALID, EXPIRING_SOON, EXPIRED, MISSING */
    private String status;

    private Long documentId;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    /** GOVERNMENT, ISSUER_PROVIDED, PROFILE */
    private String sourceCategory;

    private String issuingAuthority;

    /** Human readable instruction shown under "Get Certificate". Never contains a fabricated URL. */
    private String guidance;

    /** Real links pulled from the database only. May be empty. */
    private List<PortalLink> applyLinks = new ArrayList<>();

    /** True when this certificate currently blocks the Apply button. */
    private boolean blocking;

    public CertificateStatusDetail() {
    }

    public static class PortalLink {
        private String label;
        private String url;
        private String state;

        public PortalLink() {
        }

        public PortalLink(String label, String url, String state) {
            this.label = label;
            this.url = url;
            this.state = state;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    public Long getCertificateTypeId() {
        return certificateTypeId;
    }

    public void setCertificateTypeId(Long certificateTypeId) {
        this.certificateTypeId = certificateTypeId;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSourceCategory() {
        return sourceCategory;
    }

    public void setSourceCategory(String sourceCategory) {
        this.sourceCategory = sourceCategory;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public List<PortalLink> getApplyLinks() {
        return applyLinks;
    }

    public void setApplyLinks(List<PortalLink> applyLinks) {
        this.applyLinks = applyLinks;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
