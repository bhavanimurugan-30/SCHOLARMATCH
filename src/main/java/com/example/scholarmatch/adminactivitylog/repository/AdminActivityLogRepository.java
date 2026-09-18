package com.example.scholarmatch.adminactivitylog.repository;

import com.example.scholarmatch.adminactivitylog.mapper.AdminActivityLogRowMapper;
import com.example.scholarmatch.adminactivitylog.model.AdminActivityLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class AdminActivityLogRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AdminActivityLogRowMapper rowMapper = new AdminActivityLogRowMapper();

    public AdminActivityLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AdminActivityLog save(AdminActivityLog log) {
        String sql = "INSERT INTO admin_activity_log (admin_id, action_type, target_entity, target_id, details) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, log.getAdminId());
            ps.setString(2, log.getActionType());
            ps.setString(3, log.getTargetEntity());
            ps.setLong(4, log.getTargetId());
            ps.setString(5, log.getDetails());
            return ps;
        }, keyHolder);

        log.setActivityLogId(keyHolder.getKey().longValue());
        return log;
    }

    public List<AdminActivityLog> findByAdminId(Long adminId) {
        String sql = "SELECT * FROM admin_activity_log WHERE admin_id = ? ORDER BY action_at DESC";
        return jdbcTemplate.query(sql, rowMapper, adminId);
    }

    public List<AdminActivityLog> findByTargetEntityAndTargetId(String targetEntity, Long targetId) {
        String sql = "SELECT * FROM admin_activity_log WHERE target_entity = ? AND target_id = ? " +
                "ORDER BY action_at DESC";
        return jdbcTemplate.query(sql, rowMapper, targetEntity, targetId);
    }

    public List<AdminActivityLog> findAll() {
        String sql = "SELECT * FROM admin_activity_log ORDER BY action_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }
}