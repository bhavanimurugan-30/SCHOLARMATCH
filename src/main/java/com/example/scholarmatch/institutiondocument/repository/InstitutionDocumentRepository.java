package com.example.scholarmatch.institutiondocument.repository;

import com.example.scholarmatch.institutiondocument.mapper.InstitutionDocumentRowMapper;
import com.example.scholarmatch.institutiondocument.model.InstitutionDocument;
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
public class InstitutionDocumentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final InstitutionDocumentRowMapper rowMapper = new InstitutionDocumentRowMapper();

    public InstitutionDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public InstitutionDocument save(InstitutionDocument doc) {
        String sql = "INSERT INTO institution_document (institution_id, document_type, file_path) " +
                "VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, doc.getInstitutionId());
            ps.setString(2, doc.getDocumentType());
            ps.setString(3, doc.getFilePath());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<InstitutionDocument> findById(Long institutionDocumentId) {
        String sql = "SELECT * FROM institution_document WHERE institution_document_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, institutionDocumentId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<InstitutionDocument> findByInstitutionId(Long institutionId) {
        String sql = "SELECT * FROM institution_document WHERE institution_id = ? ORDER BY uploaded_at DESC";
        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }

    public int updateAiPrecheckConfidence(Long institutionDocumentId, Double confidence) {
        String sql = "UPDATE institution_document SET ai_precheck_confidence = ? WHERE institution_document_id = ?";
        return jdbcTemplate.update(sql, confidence, institutionDocumentId);
    }

    public int deleteById(Long institutionDocumentId) {
        String sql = "DELETE FROM institution_document WHERE institution_document_id = ?";
        return jdbcTemplate.update(sql, institutionDocumentId);
    }
}