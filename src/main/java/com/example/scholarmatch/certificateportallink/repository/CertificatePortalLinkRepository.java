package com.example.scholarmatch.certificateportallink.repository;

import com.example.scholarmatch.certificateportallink.mapper.CertificatePortalLinkRowMapper;
import com.example.scholarmatch.certificateportallink.model.CertificatePortalLink;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class CertificatePortalLinkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CertificatePortalLinkRowMapper rowMapper = new CertificatePortalLinkRowMapper();

    public CertificatePortalLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CertificatePortalLink save(CertificatePortalLink link) {
        String sql = "INSERT INTO certificate_portal_link " +
                "(certificate_type_id, state, portal_name, portal_url, last_verified_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, link.getCertificateTypeId());
            ps.setString(2, link.getState());
            ps.setString(3, link.getPortalName());
            ps.setString(4, link.getPortalUrl());
            ps.setDate(5, link.getLastVerifiedDate() != null ? Date.valueOf(link.getLastVerifiedDate()) : null);
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<CertificatePortalLink> findById(Long portalLinkId) {
        String sql = "SELECT * FROM certificate_portal_link WHERE portal_link_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, portalLinkId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<CertificatePortalLink> findByCertificateTypeId(Long certificateTypeId) {
        String sql = "SELECT * FROM certificate_portal_link WHERE certificate_type_id = ? ORDER BY state ASC";
        return jdbcTemplate.query(sql, rowMapper, certificateTypeId);
    }

    public List<CertificatePortalLink> findByCertificateTypeIdAndState(Long certificateTypeId, String state) {
        String sql = "SELECT * FROM certificate_portal_link WHERE certificate_type_id = ? AND state = ?";
        return jdbcTemplate.query(sql, rowMapper, certificateTypeId, state);
    }

    public int update(Long portalLinkId, CertificatePortalLink link) {
        String sql = "UPDATE certificate_portal_link SET state = ?, portal_name = ?, portal_url = ?, " +
                "last_verified_date = ? WHERE portal_link_id = ?";

        return jdbcTemplate.update(sql,
                link.getState(),
                link.getPortalName(),
                link.getPortalUrl(),
                link.getLastVerifiedDate() != null ? Date.valueOf(link.getLastVerifiedDate()) : null,
                portalLinkId
        );
    }

    public int deleteById(Long portalLinkId) {
        String sql = "DELETE FROM certificate_portal_link WHERE portal_link_id = ?";
        return jdbcTemplate.update(sql, portalLinkId);
    }
}