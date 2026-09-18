package com.example.scholarmatch.searchlog.mapper;

import com.example.scholarmatch.searchlog.model.SearchLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

public class SearchLogRowMapper implements RowMapper<SearchLog> {

    private final ObjectMapper objectMapper;

    public SearchLogRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SearchLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        SearchLog log = new SearchLog();
        log.setSearchId(rs.getLong("search_id"));

        long studentId = rs.getLong("student_id");
        log.setStudentId(rs.wasNull() ? null : studentId);

        log.setSearchQuery(rs.getString("search_query"));
        log.setFiltersApplied(readJsonMap(rs.getString("filters_applied")));

        if (rs.getTimestamp("searched_at") != null) {
            log.setSearchedAt(rs.getTimestamp("searched_at").toLocalDateTime());
        }

        return log;
    }

    private Map<String, Object> readJsonMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}