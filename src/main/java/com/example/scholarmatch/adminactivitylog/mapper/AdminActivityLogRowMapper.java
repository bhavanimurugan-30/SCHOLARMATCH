package com.example.scholarmatch.adminactivitylog.mapper;

import com.example.scholarmatch.adminactivitylog.model.AdminActivityLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminActivityLogRowMapper implements RowMapper<AdminActivityLog> {

    @Override
    public AdminActivityLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        AdminActivityLog log = new AdminActivityLog();
        log.setActivityLogId(rs.getLong("activity_log_id"));
        log.setAdminId(rs.getLong("admin_id"));
        log.setActionType(rs.getString("action_type"));
        log.setTargetEntity(rs.getString("target_entity"));
        log.setTargetId(rs.getLong("target_id"));
        log.setDetails(rs.getString("details"));

        if (rs.getTimestamp("action_at") != null) {
            log.setActionAt(rs.getTimestamp("action_at").toLocalDateTime());
        }

        return log;
    }
}