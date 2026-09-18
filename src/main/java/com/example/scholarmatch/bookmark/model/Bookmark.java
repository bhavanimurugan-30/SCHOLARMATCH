package com.example.scholarmatch.bookmark.model;

import java.time.LocalDateTime;

public class Bookmark {

    private Long bookmarkId;
    private Long studentId;
    private Long scholarshipId;
    private LocalDateTime bookmarkedAt;
    private LocalDateTime savedAt;
    private String status;          // SAVED, APPLIED, SAVED_AND_APPLIED
    private LocalDateTime appliedAt;

    public Bookmark() {
    }

    public Long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(Long bookmarkId) {
        this.bookmarkId = bookmarkId;
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

    public LocalDateTime getBookmarkedAt() {
        return bookmarkedAt;
    }

    public void setBookmarkedAt(LocalDateTime bookmarkedAt) {
        this.bookmarkedAt = bookmarkedAt;
    }

    public LocalDateTime getSavedAt() {
        return savedAt != null ? savedAt : bookmarkedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}