package com.example.scholarmatch.scholarship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

public class ScholarshipRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Source type is required")
    @Pattern(regexp = "GOVERNMENT|PRIVATE_CORPORATE|PRIVATE_NGO|INSTITUTION", message = "Invalid source type")
    private String sourceType;

    private String primaryCategory;

    private String fundingBodyName;

    @PositiveOrZero(message = "Amount cannot be negative")
    private Double amount;

    @Pattern(regexp = "^$|EXTERNAL_REDIRECT|INSTITUTION_MANAGED", message = "Invalid application mode")
    private String applicationMode;

    private String officialApplicationLink;

    private List<String> eligibleCategories;
    private Double minAnnualIncome;
    private Double maxAnnualIncome;
    private List<String> eligibleCourses;

    @Pattern(regexp = "^$|SCHOOL|DIPLOMA|UNDERGRADUATE|POSTGRADUATE|PHD|ANY", message = "Invalid education level")
    private String educationLevel;

    private Double minMarksCgpa;
    private List<String> eligibleStates;
    private List<String> eligibleSpecialStatus;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

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

    public Double getAmount() {
        return amount;
    }
    private List<Long> requiredDocuments;

    public List<Long> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<Long> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}