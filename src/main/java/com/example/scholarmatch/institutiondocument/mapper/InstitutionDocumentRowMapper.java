package com.example.scholarmatch.institutiondocument.mapper;

import com.example.scholarmatch.institutiondocument.model.InstitutionDocument;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionDocumentRowMapper implements RowMapper<InstitutionDocument> {

    @Override
    public InstitutionDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstitutionDocument doc = new InstitutionDocument();
        doc.setInstitutionDocumentId(rs.getLong("institution_document_id"));
        doc.setInstitutionId(rs.getLong("institution_id"));
        doc.setDocumentType(rs.getString("document_type"));
        doc.setFilePath(rs.getString("file_path"));

        double confidence = rs.getDouble("ai_precheck_confidence");
        doc.setAiPrecheckConfidence(rs.wasNull() ? null : confidence);

        if (rs.getTimestamp("uploaded_at") != null) {
            doc.setUploadedAt(rs.getTimestamp("uploaded_at").toLocalDateTime());
        }

        return doc;
    }
}