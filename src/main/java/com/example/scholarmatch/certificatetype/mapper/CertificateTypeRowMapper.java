package com.example.scholarmatch.certificatetype.mapper;

import com.example.scholarmatch.certificatetype.model.CertificateType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CertificateTypeRowMapper implements RowMapper<CertificateType> {

    private final ObjectMapper objectMapper;

    public CertificateTypeRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public CertificateType mapRow(ResultSet rs, int rowNum) throws SQLException {
        CertificateType type = new CertificateType();
        type.setCertificateTypeId(rs.getLong("certificate_type_id"));
        type.setName(rs.getString("name"));
        type.setIssuingAuthority(rs.getString("issuing_authority"));

        int minDays = rs.getInt("min_processing_days");
        type.setMinProcessingDays(rs.wasNull() ? null : minDays);

        int maxDays = rs.getInt("max_processing_days");
        type.setMaxProcessingDays(rs.wasNull() ? null : maxDays);

        type.setTatkaalAvailable(rs.getBoolean("tatkaal_available"));
        type.setOfficialApplyLink(rs.getString("official_apply_link"));
        type.setRequiredDocuments(readJsonList(rs.getString("required_documents")));

        return type;
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}