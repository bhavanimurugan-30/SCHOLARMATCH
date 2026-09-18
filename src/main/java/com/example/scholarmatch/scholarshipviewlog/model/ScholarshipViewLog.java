package com.example.scholarmatch.scholarshipviewlog.model;

import java.time.LocalDateTime;

public class ScholarshipViewLog {

    private Long viewId;
    private Long scholarshipId;
    private Long studentId;   // nullable — anonymous/untracked views
    private LocalDateTime viewedAt;

    public ScholarshipViewLog() {
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public void setScholarshipId(Long scholarshipId) {
        this.scholarshipId = scholarshipId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
}