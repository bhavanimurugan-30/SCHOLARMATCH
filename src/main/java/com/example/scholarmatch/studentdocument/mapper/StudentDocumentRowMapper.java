package com.example.scholarmatch.studentdocument.mapper;

import com.example.scholarmatch.studentdocument.model.StudentDocument;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDocumentRowMapper implements RowMapper<StudentDocument> {

    @Override
    public StudentDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
        StudentDocument doc = new StudentDocument();
        doc.setDocumentId(rs.getLong("document_id"));
        doc.setStudentId(rs.getLong("student_id"));
        doc.setCertificateTypeId(rs.getLong("certificate_type_id"));
        doc.setFilePath(rs.getString("file_path"));
        doc.setStatus(rs.getString("status"));
        doc.setOriginalFileName(rs.getString("original_file_name"));
        doc.setContentType(rs.getString("content_type"));
        long sizeBytes = rs.getLong("file_size_bytes");
        if (!rs.wasNull()) {
            doc.setFileSizeBytes(sizeBytes);
        }
        if (rs.getDate("issue_date") != null) {
            doc.setIssueDate(rs.getDate("issue_date").toLocalDate());
        }
        if (rs.getDate("expiry_date") != null) {
            doc.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        }
        if (rs.getTimestamp("uploaded_at") != null) {
            doc.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        }

        return doc;
    }
}