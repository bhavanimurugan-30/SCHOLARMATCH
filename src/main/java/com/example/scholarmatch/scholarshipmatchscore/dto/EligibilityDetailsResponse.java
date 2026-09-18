package com.example.scholarmatch.scholarshipmatchscore.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Structured eligibility payload returned by
 * GET /api/students/{studentId}/scholarships/{scholarshipId}/eligibility-details
 */
public class EligibilityDetailsResponse {

    private Long studentId;
    private Long scholarshipId;
    private String scholarshipTitle;
    private String primaryCategory;
    private String fundingBodyName;
    private Double amount;
    private LocalDate deadline;
    private String officialApplicationLink;

    private boolean eligible;
    private boolean canApply;
    private boolean expired;
    private boolean alreadyApplied;

    private Double matchPercentage;

    /** Exact, human readable reasons the student DOES meet criteria. */
    private List<String> eligibleReasons = new ArrayList<>();

    /** Exact, human readable reasons the student does NOT meet criteria. */
    private List<String> ineligibleReasons = new ArrayList<>();

    /** Reasons Apply is blocked even if criteria match (expired scholarship, missing mandatory docs...). */
    private List<String> blockingReasons = new ArrayList<>();

    /** Profile fields that are empty and therefore prevent a confident decision. */
    private List<String> missingProfileFields = new ArrayList<>();

    private List<CertificateStatusDetail> certificates = new ArrayList<>();

    private int mandatoryCertificateCount;
    private int mandatorySatisfiedCount;

    public EligibilityDetailsResponse() {
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(Long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public String getScholarshipTitle() {
        return scholarshipTitle;
    }

    public void setScholarshipTitle(String scholarshipTitle) {
        this.scholarshipTitle = scholarshipTitle;
    }

    public String getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getFundingBodyName() {
        return fundingBodyName;
    }

    public void setFundingBodyName(String fundingBodyName) {
        this.fundingBodyName = fundingBodyName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getOfficialApplicationLink() {
        return officialApplicationLink;
    }

    public void setOfficialApplicationLink(String officialApplicationLink) {
        this.officialApplicationLink = officialApplicationLink;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isCanApply() {
        return canApply;
    }

    public void setCanApply(boolean canApply) {
        this.canApply = canApply;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isAlreadyApplied() {
        return alreadyApplied;
    }

    public void setAlreadyApplied(boolean alreadyApplied) {
        this.alreadyApplied = alreadyApplied;
    }

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getEligibleReasons() {
        return eligibleReasons;
    }

    public void setEligibleReasons(List<String> eligibleReasons) {
        this.eligibleReasons = eligibleReasons;
    }

    public List<String> getIneligibleReasons() {
        return ineligibleReasons;
    }

    public void setIneligibleReasons(List<String> ineligibleReasons) {
        this.ineligibleReasons = ineligibleReasons;
    }

    public List<String> getBlockingReasons() {
        return blockingReasons;
    }

    public void setBlockingReasons(List<String> blockingReasons) {
        this.blockingReasons = blockingReasons;
    }

    public List<String> getMissingProfileFields() {
        return missingProfileFields;
    }

    public void setMissingProfileFields(List<String> missingProfileFields) {
        this.missingProfileFields = missingProfileFields;
    }

    public List<CertificateStatusDetail> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificateStatusDetail> certificates) {
        this.certificates = certificates;
    }

    public int getMandatoryCertificateCount() {
        return mandatoryCertificateCount;
    }

    public void setMandatoryCertificateCount(int mandatoryCertificateCount) {
        this.mandatoryCertificateCount = mandatoryCertificateCount;
    }

    public int getMandatorySatisfiedCount() {
        return mandatorySatisfiedCount;
    }

    public void setMandatorySatisfiedCount(int mandatorySatisfiedCount) {
        this.mandatorySatisfiedCount = mandatorySatisfiedCount;
    }
}
