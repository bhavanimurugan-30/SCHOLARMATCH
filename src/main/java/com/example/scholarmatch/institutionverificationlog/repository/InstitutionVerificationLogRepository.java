package com.example.scholarmatch.institutionverificationlog.repository;

import com.example.scholarmatch.institutionverificationlog.mapper.InstitutionVerificationLogRowMapper;
import com.example.scholarmatch.institutionverificationlog.model.InstitutionVerificationLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

@Repository
public class InstitutionVerificationLogRepository {

    private final JdbcTemplate jdbcTemplate;
    private final InstitutionVerificationLogRowMapper rowMapper = new InstitutionVerificationLogRowMapper();

    public InstitutionVerificationLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public InstitutionVerificationLog save(InstitutionVerificationLog log) {
        String sql = "INSERT INTO institution_verification_log (institution_id, admin_id, action, reason) " +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, log.getInstitutionId());
            if (log.getAdminId() != null) {
                ps.setLong(2, log.getAdminId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, log.getAction());
            ps.setString(4, log.getReason());
            return ps;
        }, keyHolder);

        long generatedId = keyHolder.getKey().longValue();
        return findByInstitutionId(log.getInstitutionId()).stream()
                .filter(l -> l.getLogId().equals(generatedId))
                .findFirst()
                .orElseThrow();
    }

    public List<InstitutionVerificationLog> findByInstitutionId(Long institutionId) {
        String sql = "SELECT * FROM institution_verification_log WHERE institution_id = ? ORDER BY action_at DESC";
        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }
}