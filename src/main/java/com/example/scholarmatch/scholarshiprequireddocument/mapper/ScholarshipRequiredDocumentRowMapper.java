package com.example.scholarmatch.scholarshiprequireddocument.mapper;

import com.example.scholarmatch.scholarshiprequireddocument.model.ScholarshipRequiredDocument;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScholarshipRequiredDocumentRowMapper implements RowMapper<ScholarshipRequiredDocument> {

    @Override
    public ScholarshipRequiredDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScholarshipRequiredDocument doc = new ScholarshipRequiredDocument();
        doc.setScholarshipId(rs.getLong("scholarship_id"));
        doc.setCertificateTypeId(rs.getLong("certificate_type_id"));
        doc.setMandatory(rs.getBoolean("is_mandatory"));
        return doc;
    }
}