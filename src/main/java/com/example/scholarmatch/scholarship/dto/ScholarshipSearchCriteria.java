package com.example.scholarmatch.scholarship.dto;

public class ScholarshipSearchCriteria {

    private String keyword;
    private String category;
    private String state;
    private String course;
    private Double income;
    private String sourceType;
    private Double minAmount;
    private Double maxAmount;
    private java.time.LocalDate deadlineBefore;
    private Long studentId;
    private Double minMatchPercentage;
    private String sortBy;   // deadline | amount | match
    private String sortDir;  // asc | desc

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public Double getMinAmount() { return minAmount; }
    public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }

    public Double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }

    public java.time.LocalDate getDeadlineBefore() { return deadlineBefore; }
    public void setDeadlineBefore(java.time.LocalDate deadlineBefore) { this.deadlineBefore = deadlineBefore; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Double getMinMatchPercentage() { return minMatchPercentage; }
    public void setMinMatchPercentage(Double minMatchPercentage) { this.minMatchPercentage = minMatchPercentage; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
}