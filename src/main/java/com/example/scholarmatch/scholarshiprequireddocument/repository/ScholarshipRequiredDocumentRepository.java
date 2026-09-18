package com.example.scholarmatch.scholarshiprequireddocument.repository;

import com.example.scholarmatch.scholarshiprequireddocument.mapper.ScholarshipRequiredDocumentRowMapper;
import com.example.scholarmatch.scholarshiprequireddocument.model.ScholarshipRequiredDocument;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScholarshipRequiredDocumentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ScholarshipRequiredDocumentRowMapper rowMapper = new ScholarshipRequiredDocumentRowMapper();

    public ScholarshipRequiredDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ScholarshipRequiredDocument save(ScholarshipRequiredDocument doc) {
        String sql = "INSERT INTO scholarship_required_document (scholarship_id, certificate_type_id, is_mandatory) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, doc.getScholarshipId(), doc.getCertificateTypeId(), doc.isMandatory());
        return doc;
    }

    public List<ScholarshipRequiredDocument> findByScholarshipId(Long scholarshipId) {
        String sql = "SELECT * FROM scholarship_required_document WHERE scholarship_id = ?";
        return jdbcTemplate.query(sql, rowMapper, scholarshipId);
    }

    public boolean exists(Long scholarshipId, Long certificateTypeId) {
        String sql = "SELECT COUNT(*) FROM scholarship_required_document " +
                "WHERE scholarship_id = ? AND certificate_type_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, scholarshipId, certificateTypeId);
        return count != null && count > 0;
    }

    public int deleteByScholarshipIdAndCertificateTypeId(Long scholarshipId, Long certificateTypeId) {
        String sql = "DELETE FROM scholarship_required_document WHERE scholarship_id = ? AND certificate_type_id = ?";
        return jdbcTemplate.update(sql, scholarshipId, certificateTypeId);
    }

    public int deleteByScholarshipId(Long scholarshipId) {
        String sql = "DELETE FROM scholarship_required_document WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, scholarshipId);
    }
}