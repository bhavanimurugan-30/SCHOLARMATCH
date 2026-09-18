package com.example.scholarmatch.searchlog.dto;

import java.util.Map;

public class SearchLogRequest {

    private Long studentId;
    private String searchQuery;
    private Map<String, Object> filtersApplied;

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
}