package com.example.scholarmatch.admin.repository;

import com.example.scholarmatch.admin.mapper.AdminRowMapper;
import com.example.scholarmatch.admin.model.Admin;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AdminRowMapper rowMapper = new AdminRowMapper();

    public AdminRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Admin save(Admin admin) {
        String sql = "INSERT INTO admin (full_name, email, password_hash, role, is_active) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, admin.getFullName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPasswordHash());
            ps.setString(4, admin.getRole());
            ps.setBoolean(5, admin.isActive());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<Admin> findById(Long adminId) {
        String sql = "SELECT * FROM admin WHERE admin_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, adminId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, email));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Admin> findAll() {
        String sql = "SELECT * FROM admin ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM admin WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public int update(Long adminId, Admin admin) {
        String sql = "UPDATE admin SET full_name = ?, email = ?, role = ? WHERE admin_id = ?";
        return jdbcTemplate.update(sql, admin.getFullName(), admin.getEmail(), admin.getRole(), adminId);
    }

    public int updateActiveStatus(Long adminId, boolean active) {
        String sql = "UPDATE admin SET is_active = ? WHERE admin_id = ?";
        return jdbcTemplate.update(sql, active, adminId);
    }

    public int deleteById(Long adminId) {
        String sql = "DELETE FROM admin WHERE admin_id = ?";
        return jdbcTemplate.update(sql, adminId);
    }
}