package com.example.scholarmatch.admin.mapper;

import com.example.scholarmatch.admin.model.Admin;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRowMapper implements RowMapper<Admin> {

    @Override
    public Admin mapRow(ResultSet rs, int rowNum) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getLong("admin_id"));
        admin.setFullName(rs.getString("full_name"));
        admin.setEmail(rs.getString("email"));
        admin.setPasswordHash(rs.getString("password_hash"));
        admin.setRole(rs.getString("role"));
        admin.setActive(rs.getBoolean("is_active"));

        if (rs.getTimestamp("created_at") != null) {
            admin.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            admin.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return admin;
    }
}