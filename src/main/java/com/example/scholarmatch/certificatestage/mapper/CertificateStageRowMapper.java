package com.example.scholarmatch.certificatestage.mapper;

import com.example.scholarmatch.certificatestage.model.CertificateStage;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificateStageRowMapper implements RowMapper<CertificateStage> {

    @Override
    public CertificateStage mapRow(ResultSet rs, int rowNum) throws SQLException {
        CertificateStage stage = new CertificateStage();
        stage.setStageId(rs.getLong("stage_id"));
        stage.setCertificateTypeId(rs.getLong("certificate_type_id"));
        stage.setStageName(rs.getString("stage_name"));

        int minDays = rs.getInt("min_days");
        stage.setMinDays(rs.wasNull() ? null : minDays);

        int maxDays = rs.getInt("max_days");
        stage.setMaxDays(rs.wasNull() ? null : maxDays);

        stage.setSequenceOrder(rs.getInt("sequence_order"));

        return stage;
    }
}