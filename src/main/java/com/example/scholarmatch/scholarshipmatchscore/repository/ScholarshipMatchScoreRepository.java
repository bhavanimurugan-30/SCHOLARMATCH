package com.example.scholarmatch.scholarshipmatchscore.repository;

import com.example.scholarmatch.scholarshipmatchscore.mapper.ScholarshipMatchScoreRowMapper;
import com.example.scholarmatch.scholarshipmatchscore.model.ScholarshipMatchScore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ScholarshipMatchScoreRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final ScholarshipMatchScoreRowMapper rowMapper;

    public ScholarshipMatchScoreRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rowMapper = new ScholarshipMatchScoreRowMapper(objectMapper);
    }

    public ScholarshipMatchScore upsert(ScholarshipMatchScore m) {
        String sql = "INSERT INTO scholarship_match_score " +
                "(student_id, scholarship_id, match_percentage, matched_criteria, unmatched_criteria, ai_explanation_text) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE match_percentage = VALUES(match_percentage), " +
                "matched_criteria = VALUES(matched_criteria), unmatched_criteria = VALUES(unmatched_criteria), " +
                "ai_explanation_text = VALUES(ai_explanation_text)";

        jdbcTemplate.update(sql,
                m.getStudentId(),
                m.getScholarshipId(),
                m.getMatchPercentage(),
                writeJson(m.getMatchedCriteria()),
                writeJson(m.getUnmatchedCriteria()),
                m.getAiExplanationText()
        );

        return findByStudentIdAndScholarshipId(m.getStudentId(), m.getScholarshipId()).orElseThrow();
    }

    public Optional<ScholarshipMatchScore> findByStudentIdAndScholarshipId(Long studentId, Long scholarshipId) {
        String sql = "SELECT * FROM scholarship_match_score WHERE student_id = ? AND scholarship_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, studentId, scholarshipId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * All match scores for a student, highest match first — powers the personalized
     * dashboard sort order described in Section 4.1 / 5.1.
     */
    public List<ScholarshipMatchScore> findByStudentIdOrderByMatchDesc(Long studentId) {
        String sql = "SELECT * FROM scholarship_match_score WHERE student_id = ? ORDER BY match_percentage DESC";
        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    public List<ScholarshipMatchScore> findByScholarshipId(Long scholarshipId) {
        String sql = "SELECT * FROM scholarship_match_score WHERE scholarship_id = ? ORDER BY match_percentage DESC";
        return jdbcTemplate.query(sql, rowMapper, scholarshipId);
    }

    private String writeJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }
}