package com.example.scholarmatch.studentdocument.repository;

import com.example.scholarmatch.studentdocument.mapper.StudentDocumentRowMapper;
import com.example.scholarmatch.studentdocument.model.StudentDocument;
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
public class StudentDocumentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final StudentDocumentRowMapper rowMapper = new StudentDocumentRowMapper();

    public StudentDocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public StudentDocument save(StudentDocument doc) {
        String sql = "INSERT INTO student_document " +
                "(student_id, certificate_type_id, file_path, status, issue_date, expiry_date, " +
                "original_file_name, content_type, file_size_bytes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, doc.getStudentId());
            ps.setLong(2, doc.getCertificateTypeId());
            ps.setString(3, doc.getFilePath());
            ps.setString(4, doc.getStatus() != null ? doc.getStatus() : "VALID");
            ps.setDate(5, doc.getIssueDate() != null ? Date.valueOf(doc.getIssueDate()) : null);
            ps.setDate(6, doc.getExpiryDate() != null ? Date.valueOf(doc.getExpiryDate()) : null);
            ps.setString(7, doc.getOriginalFileName());
            ps.setString(8, doc.getContentType());
            if (doc.getFileSizeBytes() != null) {
                ps.setLong(9, doc.getFileSizeBytes());
            } else {
                ps.setNull(9, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<StudentDocument> findById(Long documentId) {
        String sql = "SELECT * FROM student_document WHERE document_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, documentId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<StudentDocument> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM student_document WHERE student_id = ? ORDER BY uploaded_at DESC";
        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    /**
     * Most recent document a student holds for one certificate type.
     * Used so that re-uploading a certificate replaces the previous one instead of duplicating it.
     */
    public Optional<StudentDocument> findByStudentIdAndCertificateTypeId(Long studentId, Long certificateTypeId) {
        String sql = "SELECT * FROM student_document WHERE student_id = ? AND certificate_type_id = ? " +
                "ORDER BY uploaded_at DESC, document_id DESC LIMIT 1";
        List<StudentDocument> results = jdbcTemplate.query(sql, rowMapper, studentId, certificateTypeId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /** Full replacement of an existing certificate record, including file metadata. */
    public int replaceDocument(Long documentId, StudentDocument doc) {
        String sql = "UPDATE student_document SET file_path = ?, original_file_name = ?, content_type = ?, " +
                "file_size_bytes = ?, status = ?, issue_date = ?, expiry_date = ?, uploaded_at = NOW() " +
                "WHERE document_id = ?";
        return jdbcTemplate.update(sql,
                doc.getFilePath(),
                doc.getOriginalFileName(),
                doc.getContentType(),
                doc.getFileSizeBytes(),
                doc.getStatus(),
                doc.getIssueDate(),
                doc.getExpiryDate(),
                documentId);
    }

    public List<StudentDocument> findExpiringBefore(Date cutoffDate) {
        String sql = "SELECT * FROM student_document WHERE expiry_date IS NOT NULL AND expiry_date <= ? " +
                "AND status != 'EXPIRED'";
        return jdbcTemplate.query(sql, rowMapper, cutoffDate);
    }

    public int updateStatus(Long documentId, String status) {
        String sql = "UPDATE student_document SET status = ? WHERE document_id = ?";
        return jdbcTemplate.update(sql, status, documentId);
    }

    public int update(Long documentId, StudentDocument doc) {
        String sql = "UPDATE student_document SET file_path = ?, status = ?, issue_date = ?, expiry_date = ? " +
                "WHERE document_id = ?";

        return jdbcTemplate.update(sql,
                doc.getFilePath(),
                doc.getStatus(),
                doc.getIssueDate() != null ? Date.valueOf(doc.getIssueDate()) : null,
                doc.getExpiryDate() != null ? Date.valueOf(doc.getExpiryDate()) : null,
                documentId
        );
    }

    public int deleteById(Long documentId) {
        String sql = "DELETE FROM student_document WHERE document_id = ?";
        return jdbcTemplate.update(sql, documentId);
    }
}