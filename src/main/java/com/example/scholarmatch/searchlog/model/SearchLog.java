package com.example.scholarmatch.searchlog.model;

import java.time.LocalDateTime;
import java.util.Map;

public class SearchLog {

    private Long searchId;
    private Long studentId;          // nullable
    private String searchQuery;
    private Map<String, Object> filtersApplied;
    private LocalDateTime searchedAt;

    public SearchLog() {
    }

    public Long getSearchId() {
        return searchId;
    }

    public void setSearchId(Long searchId) {
        this.searchId = searchId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Map<String, Object> getFiltersApplied() {
        return filtersApplied;
    }

    public void setFiltersApplied(Map<String, Object> filtersApplied) {
        this.filtersApplied = filtersApplied;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(LocalDateTime searchedAt) {
        this.searchedAt = searchedAt;
    }
}