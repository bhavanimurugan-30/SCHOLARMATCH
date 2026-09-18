package com.example.scholarmatch.institution.repository;

import com.example.scholarmatch.institution.mapper.InstitutionRowMapper;
import com.example.scholarmatch.institution.model.Institution;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class InstitutionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final InstitutionRowMapper rowMapper = new InstitutionRowMapper();

    public InstitutionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Institution save(Institution institution) {
        String sql = "INSERT INTO institution " +
                "(institution_name, institution_type, email, password_hash, website_url, state, district, " +
                "pan_number, registration_number, verification_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, institution.getInstitutionName());
            ps.setString(2, institution.getInstitutionType());
            ps.setString(3, institution.getEmail());
            ps.setString(4, institution.getPasswordHash());
            ps.setString(5, institution.getWebsiteUrl());
            ps.setString(6, institution.getState());
            ps.setString(7, institution.getDistrict());
            ps.setString(8, institution.getPanNumber());
            ps.setString(9, institution.getRegistrationNumber());
            ps.setString(10, institution.getVerificationStatus() != null
                    ? institution.getVerificationStatus() : "PENDING");
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<Institution> findById(Long institutionId) {
        String sql = "SELECT * FROM institution WHERE institution_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, institutionId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Institution> findByEmail(String email) {
        String sql = "SELECT * FROM institution WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, email));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Institution> findAll() {
        String sql = "SELECT * FROM institution ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Institution> findByVerificationStatus(String status) {
        String sql = "SELECT * FROM institution WHERE verification_status = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM institution WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT COUNT(*) FROM institution WHERE registration_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, registrationNumber);
        return count != null && count > 0;
    }

    public int update(Long institutionId, Institution institution) {
        String sql = "UPDATE institution SET institution_name = ?, institution_type = ?, website_url = ?, " +
                "state = ?, district = ?, pan_number = ?, registration_number = ? WHERE institution_id = ?";

        return jdbcTemplate.update(sql,
                institution.getInstitutionName(),
                institution.getInstitutionType(),
                institution.getWebsiteUrl(),
                institution.getState(),
                institution.getDistrict(),
                institution.getPanNumber(),
                institution.getRegistrationNumber(),
                institutionId
        );
    }

    public int updateVerificationStatus(Long institutionId, String status) {
        String sql = "UPDATE institution SET verification_status = ? WHERE institution_id = ?";
        return jdbcTemplate.update(sql, status, institutionId);
    }

    public int deleteById(Long institutionId) {
        String sql = "DELETE FROM institution WHERE institution_id = ?";
        return jdbcTemplate.update(sql, institutionId);
    }
    public int updatePasswordHash(Long institutionId, String passwordHash) {
        String sql = "UPDATE institution SET password_hash = ? WHERE institution_id = ?";
        return jdbcTemplate.update(sql, passwordHash, institutionId);
    }
}