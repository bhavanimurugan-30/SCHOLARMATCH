package com.example.scholarmatch.institutionverificationlog.mapper;

import com.example.scholarmatch.institutionverificationlog.model.InstitutionVerificationLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionVerificationLogRowMapper implements RowMapper<InstitutionVerificationLog> {

    @Override
    public InstitutionVerificationLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstitutionVerificationLog log = new InstitutionVerificationLog();
        log.setLogId(rs.getLong("log_id"));
        log.setInstitutionId(rs.getLong("institution_id"));

        long adminId = rs.getLong("admin_id");
        log.setAdminId(rs.wasNull() ? null : adminId);

        log.setAction(rs.getString("action"));
        log.setReason(rs.getString("reason"));

        if (rs.getTimestamp("action_at") != null) {
            log.setActionAt(rs.getTimestamp("action_at").toLocalDateTime());
        }

        return log;
    }
}