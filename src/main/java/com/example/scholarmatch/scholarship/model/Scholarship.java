package com.example.scholarmatch.scholarship.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Scholarship {

    private Long scholarshipId;
    private String title;
    private String description;
    private String sourceType;                  // GOVERNMENT, PRIVATE_CORPORATE, PRIVATE_NGO, INSTITUTION
    private String primaryCategory;             // WOMENS, COLLEGE, COMPANY, STATE, CENTRAL
    private String fundingBodyName;
    private Long providerInstitutionId;          // set only when sourceType = INSTITUTION
    private Double amount;
    private String applicationMode;              // EXTERNAL_REDIRECT, INSTITUTION_MANAGED
    private String officialApplicationLink;

    // Eligibility criteria consumed by the weighted match-scoring engine
    private List<String> eligibleCategories;
    private Double minAnnualIncome;
    private Double maxAnnualIncome;
    private List<String> eligibleCourses;
    private String educationLevel;               // SCHOOL, DIPLOMA, UNDERGRADUATE, POSTGRADUATE, PHD, ANY
    private Double minMarksCgpa;
    private List<String> eligibleStates;
    private List<String> eligibleSpecialStatus;

    private LocalDate deadline;
    private String approvalStatus;                // PENDING_APPROVAL, APPROVED, REJECTED
    private boolean active;

    private Long submittedByAdminId;
    private Long submittedByInstitutionId;
    private Long approvedByAdminId;
    private String rejectionReason;

    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Scholarship() {
    }

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(Long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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

    public Long getProviderInstitutionId() {
        return providerInstitutionId;
    }

    public void setProviderInstitutionId(Long providerInstitutionId) {
        this.providerInstitutionId = providerInstitutionId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(String applicationMode) {
        this.applicationMode = applicationMode;
    }

    public String getOfficialApplicationLink() {
        return officialApplicationLink;
    }

    public void setOfficialApplicationLink(String officialApplicationLink) {
        this.officialApplicationLink = officialApplicationLink;
    }

    public List<String> getEligibleCategories() {
        return eligibleCategories;
    }

    public void setEligibleCategories(List<String> eligibleCategories) {
        this.eligibleCategories = eligibleCategories;
    }

    public Double getMinAnnualIncome() {
        return minAnnualIncome;
    }

    public void setMinAnnualIncome(Double minAnnualIncome) {
        this.minAnnualIncome = minAnnualIncome;
    }

    public Double getMaxAnnualIncome() {
        return maxAnnualIncome;
    }

    public void setMaxAnnualIncome(Double maxAnnualIncome) {
        this.maxAnnualIncome = maxAnnualIncome;
    }

    public List<String> getEligibleCourses() {
        return eligibleCourses;
    }

    public void setEligibleCourses(List<String> eligibleCourses) {
        this.eligibleCourses = eligibleCourses;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Double getMinMarksCgpa() {
        return minMarksCgpa;
    }

    public void setMinMarksCgpa(Double minMarksCgpa) {
        this.minMarksCgpa = minMarksCgpa;
    }

    public List<String> getEligibleStates() {
        return eligibleStates;
    }

    public void setEligibleStates(List<String> eligibleStates) {
        this.eligibleStates = eligibleStates;
    }

    public List<String> getEligibleSpecialStatus() {
        return eligibleSpecialStatus;
    }

    public void setEligibleSpecialStatus(List<String> eligibleSpecialStatus) {
        this.eligibleSpecialStatus = eligibleSpecialStatus;
    }

    // Populated only by ScholarshipRepository.search() when a studentId filter is given.
    // Not persisted — ignored by save()/update() SQL.
    private Double matchPercentage;

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getSubmittedByAdminId() {
        return submittedByAdminId;
    }

    public void setSubmittedByAdminId(Long submittedByAdminId) {
        this.submittedByAdminId = submittedByAdminId;
    }

    public Long getSubmittedByInstitutionId() {
        return submittedByInstitutionId;
    }

    public void setSubmittedByInstitutionId(Long submittedByInstitutionId) {
        this.submittedByInstitutionId = submittedByInstitutionId;
    }

    public Long getApprovedByAdminId() {
        return approvedByAdminId;
    }

    public void setApprovedByAdminId(Long approvedByAdminId) {
        this.approvedByAdminId = approvedByAdminId;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    private List<Long> requiredDocuments;   // certificate_type_id list required for this scholarship

    public List<Long> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Long> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }
}