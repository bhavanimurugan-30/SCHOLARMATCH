package com.example.scholarmatch.searchlog.repository;

import com.example.scholarmatch.searchlog.mapper.SearchLogRowMapper;
import com.example.scholarmatch.searchlog.model.SearchLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Repository
public class SearchLogRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final SearchLogRowMapper rowMapper;

    public SearchLogRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rowMapper = new SearchLogRowMapper(objectMapper);
    }

    public SearchLog save(SearchLog log) {
        String sql = "INSERT INTO search_log (student_id, search_query, filters_applied) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (log.getStudentId() != null) {
                ps.setLong(1, log.getStudentId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            ps.setString(2, log.getSearchQuery());
            ps.setString(3, writeJson(log.getFiltersApplied()));
            return ps;
        }, keyHolder);

        log.setSearchId(keyHolder.getKey().longValue());
        return log;
    }

    public List<SearchLog> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM search_log WHERE student_id = ? ORDER BY searched_at DESC";
        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    /**
     * "Most-searched" analytics for the admin dashboard (Section 4.2).
     */
    public List<Map<String, Object>> findMostSearchedQueries(int limit) {
        String sql = "SELECT search_query, COUNT(*) AS search_total FROM search_log " +
                "WHERE search_query IS NOT NULL AND search_query != '' " +
                "GROUP BY search_query ORDER BY search_total DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, limit);
    }

    private String writeJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map != null ? map : Map.of());
        } catch (Exception e) {
            return "{}";
        }
    }
    public List<SearchLog> findRecent(int limit) {
        String sql = "SELECT * FROM search_log ORDER BY searched_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, rowMapper, limit);
    }
}