package com.example.scholarmatch.scholarshipmatchscore.model;

import java.time.LocalDateTime;
import java.util.List;

public class ScholarshipMatchScore {

    private Long matchScoreId;
    private Long studentId;
    private Long scholarshipId;
    private Double matchPercentage;
    private List<String> matchedCriteria;
    private List<String> unmatchedCriteria;
    private String aiExplanationText;
    private LocalDateTime computedAt;

    public ScholarshipMatchScore() {
    }

    public Long getMatchScoreId() {
        return matchScoreId;
    }

    public void setMatchScoreId(Long matchScoreId) {
        this.matchScoreId = matchScoreId;
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

    public Double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(Double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchedCriteria() {
        return matchedCriteria;
    }

    public void setMatchedCriteria(List<String> matchedCriteria) {
        this.matchedCriteria = matchedCriteria;
    }

    public List<String> getUnmatchedCriteria() {
        return unmatchedCriteria;
    }

    public void setUnmatchedCriteria(List<String> unmatchedCriteria) {
        this.unmatchedCriteria = unmatchedCriteria;
    }

    public String getAiExplanationText() {
        return aiExplanationText;
    }

    public void setAiExplanationText(String aiExplanationText) {
        this.aiExplanationText = aiExplanationText;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }
}