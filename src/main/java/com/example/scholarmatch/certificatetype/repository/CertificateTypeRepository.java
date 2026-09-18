package com.example.scholarmatch.certificatetype.repository;

import com.example.scholarmatch.certificatetype.mapper.CertificateTypeRowMapper;
import com.example.scholarmatch.certificatetype.model.CertificateType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class CertificateTypeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final CertificateTypeRowMapper rowMapper;

    public CertificateTypeRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rowMapper = new CertificateTypeRowMapper(objectMapper);
    }

    public CertificateType save(CertificateType type) {
        String sql = "INSERT INTO certificate_type " +
                "(name, issuing_authority, min_processing_days, max_processing_days, tatkaal_available, " +
                "official_apply_link, required_documents) VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, type.getName());
            ps.setString(2, type.getIssuingAuthority());
            if (type.getMinProcessingDays() != null) {
                ps.setInt(3, type.getMinProcessingDays());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            if (type.getMaxProcessingDays() != null) {
                ps.setInt(4, type.getMaxProcessingDays());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setBoolean(5, type.isTatkaalAvailable());
            ps.setString(6, type.getOfficialApplyLink());
            ps.setString(7, writeJson(type.getRequiredDocuments()));
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<CertificateType> findById(Long certificateTypeId) {
        String sql = "SELECT * FROM certificate_type WHERE certificate_type_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, certificateTypeId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<CertificateType> findByName(String name) {
        String sql = "SELECT * FROM certificate_type WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, name));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<CertificateType> findAll() {
        String sql = "SELECT * FROM certificate_type ORDER BY name ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM certificate_type WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public int update(Long certificateTypeId, CertificateType type) {
        String sql = "UPDATE certificate_type SET name = ?, issuing_authority = ?, min_processing_days = ?, " +
                "max_processing_days = ?, tatkaal_available = ?, official_apply_link = ?, required_documents = ? " +
                "WHERE certificate_type_id = ?";

        return jdbcTemplate.update(sql,
                type.getName(),
                type.getIssuingAuthority(),
                type.getMinProcessingDays(),
                type.getMaxProcessingDays(),
                type.isTatkaalAvailable(),
                type.getOfficialApplyLink(),
                writeJson(type.getRequiredDocuments()),
                certificateTypeId
        );
    }

    public int deleteById(Long certificateTypeId) {
        String sql = "DELETE FROM certificate_type WHERE certificate_type_id = ?";
        return jdbcTemplate.update(sql, certificateTypeId);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value != null ? value : java.util.List.of());
        } catch (Exception e) {
            return "[]";
        }
    }
}