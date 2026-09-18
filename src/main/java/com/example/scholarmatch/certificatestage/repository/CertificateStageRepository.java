package com.example.scholarmatch.certificatestage.repository;

import com.example.scholarmatch.certificatestage.mapper.CertificateStageRowMapper;
import com.example.scholarmatch.certificatestage.model.CertificateStage;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class CertificateStageRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CertificateStageRowMapper rowMapper = new CertificateStageRowMapper();

    public CertificateStageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CertificateStage save(CertificateStage stage) {
        String sql = "INSERT INTO certificate_stage " +
                "(certificate_type_id, stage_name, min_days, max_days, sequence_order) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, stage.getCertificateTypeId());
            ps.setString(2, stage.getStageName());
            if (stage.getMinDays() != null) {
                ps.setInt(3, stage.getMinDays());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (stage.getMaxDays() != null) {
                ps.setInt(4, stage.getMaxDays());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, stage.getSequenceOrder());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<CertificateStage> findById(Long stageId) {
        String sql = "SELECT * FROM certificate_stage WHERE stage_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, stageId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<CertificateStage> findByCertificateTypeId(Long certificateTypeId) {
        String sql = "SELECT * FROM certificate_stage WHERE certificate_type_id = ? ORDER BY sequence_order ASC";
        return jdbcTemplate.query(sql, rowMapper, certificateTypeId);
    }

    public int update(Long stageId, CertificateStage stage) {
        String sql = "UPDATE certificate_stage SET stage_name = ?, min_days = ?, max_days = ?, " +
                "sequence_order = ? WHERE stage_id = ?";

        return jdbcTemplate.update(sql,
                stage.getStageName(),
                stage.getMinDays(),
                stage.getMaxDays(),
                stage.getSequenceOrder(),
                stageId
        );
    }

    public int deleteById(Long stageId) {
        String sql = "DELETE FROM certificate_stage WHERE stage_id = ?";
        return jdbcTemplate.update(sql, stageId);
    }

    public int deleteByCertificateTypeId(Long certificateTypeId) {
        String sql = "DELETE FROM certificate_stage WHERE certificate_type_id = ?";
        return jdbcTemplate.update(sql, certificateTypeId);
    }
}