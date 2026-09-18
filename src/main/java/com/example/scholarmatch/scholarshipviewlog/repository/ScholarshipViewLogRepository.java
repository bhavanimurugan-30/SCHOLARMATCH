package com.example.scholarmatch.scholarshipviewlog.repository;

import com.example.scholarmatch.scholarshipviewlog.mapper.ScholarshipViewLogRowMapper;
import com.example.scholarmatch.scholarshipviewlog.model.ScholarshipViewLog;
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
public class ScholarshipViewLogRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ScholarshipViewLogRowMapper rowMapper = new ScholarshipViewLogRowMapper();

    public ScholarshipViewLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ScholarshipViewLog save(ScholarshipViewLog log) {
        if (log.getStudentId() != null) {
            String sql = "INSERT INTO scholarship_view_log (scholarship_id, student_id, viewed_at) VALUES (?, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE viewed_at = NOW()";
            jdbcTemplate.update(sql, log.getScholarshipId(), log.getStudentId());
            return log;
        }

        String sql = "INSERT INTO scholarship_view_log (scholarship_id, student_id) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, log.getScholarshipId());
            ps.setNull(2, Types.BIGINT);
            return ps;
        }, keyHolder);

        log.setViewId(keyHolder.getKey().longValue());
        return log;
    }

    public List<ScholarshipViewLog> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM scholarship_view_log WHERE student_id = ? ORDER BY viewed_at DESC";
        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    public List<ScholarshipViewLog> findByScholarshipId(Long scholarshipId) {
        String sql = "SELECT * FROM scholarship_view_log WHERE scholarship_id = ? ORDER BY viewed_at DESC";
        return jdbcTemplate.query(sql, rowMapper, scholarshipId);
    }

    /**
     * "Most-viewed scholarships" analytics for the admin dashboard (Section 4.2).
     */
    public List<Map<String, Object>> findMostViewed(int limit) {
        String sql = "SELECT scholarship_id, COUNT(*) AS view_total FROM scholarship_view_log " +
                "GROUP BY scholarship_id ORDER BY view_total DESC LIMIT ?";
        return jdbcTemplate.queryForList(sql, limit);
    }

    public List<ScholarshipViewLog> findRecent(int limit) {
        String sql = "SELECT * FROM scholarship_view_log ORDER BY viewed_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, rowMapper, limit);
    }

    public int countByScholarshipId(Long scholarshipId) {
        String sql = "SELECT COUNT(*) FROM scholarship_view_log WHERE scholarship_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, scholarshipId);
        return count != null ? count : 0;
    }
}