package com.example.scholarmatch.scholarshipmatchscore.mapper;

import com.example.scholarmatch.scholarshipmatchscore.model.ScholarshipMatchScore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ScholarshipMatchScoreRowMapper implements RowMapper<ScholarshipMatchScore> {

    private final ObjectMapper objectMapper;

    public ScholarshipMatchScoreRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ScholarshipMatchScore mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScholarshipMatchScore m = new ScholarshipMatchScore();
        m.setMatchScoreId(rs.getLong("match_score_id"));
        m.setStudentId(rs.getLong("student_id"));
        m.setScholarshipId(rs.getLong("scholarship_id"));
        m.setMatchPercentage(rs.getDouble("match_percentage"));
        m.setMatchedCriteria(readJsonList(rs.getString("matched_criteria")));
        m.setUnmatchedCriteria(readJsonList(rs.getString("unmatched_criteria")));
        m.setAiExplanationText(rs.getString("ai_explanation_text"));

        if (rs.getTimestamp("computed_at") != null) {
            m.setComputedAt(rs.getTimestamp("computed_at").toLocalDateTime());
        }

        return m;
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}