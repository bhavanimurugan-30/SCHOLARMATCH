package com.example.scholarmatch.institutioncontact.repository;

import com.example.scholarmatch.institutioncontact.mapper.InstitutionContactPersonRowMapper;
import com.example.scholarmatch.institutioncontact.model.InstitutionContactPerson;
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
public class InstitutionContactPersonRepository {

    private final JdbcTemplate jdbcTemplate;
    private final InstitutionContactPersonRowMapper rowMapper = new InstitutionContactPersonRowMapper();

    public InstitutionContactPersonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public InstitutionContactPerson save(InstitutionContactPerson contact) {
        String sql = "INSERT INTO institution_contact_person " +
                "(institution_id, full_name, designation, phone, email) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, contact.getInstitutionId());
            ps.setString(2, contact.getFullName());
            ps.setString(3, contact.getDesignation());
            ps.setString(4, contact.getPhone());
            ps.setString(5, contact.getEmail());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<InstitutionContactPerson> findById(Long contactPersonId) {
        String sql = "SELECT * FROM institution_contact_person WHERE contact_person_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, contactPersonId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<InstitutionContactPerson> findByInstitutionId(Long institutionId) {
        String sql = "SELECT * FROM institution_contact_person WHERE institution_id = ?";
        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }

    public int update(Long contactPersonId, InstitutionContactPerson contact) {
        String sql = "UPDATE institution_contact_person SET full_name = ?, designation = ?, phone = ?, " +
                "email = ? WHERE contact_person_id = ?";

        return jdbcTemplate.update(sql,
                contact.getFullName(),
                contact.getDesignation(),
                contact.getPhone(),
                contact.getEmail(),
                contactPersonId
        );
    }

    public int deleteById(Long contactPersonId) {
        String sql = "DELETE FROM institution_contact_person WHERE contact_person_id = ?";
        return jdbcTemplate.update(sql, contactPersonId);
    }
}