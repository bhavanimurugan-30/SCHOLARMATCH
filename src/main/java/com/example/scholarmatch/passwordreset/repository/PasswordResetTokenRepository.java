package com.example.scholarmatch.passwordreset.repository;

import com.example.scholarmatch.passwordreset.mapper.PasswordResetTokenRowMapper;
import com.example.scholarmatch.passwordreset.model.PasswordResetToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class PasswordResetTokenRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordResetTokenRowMapper rowMapper = new PasswordResetTokenRowMapper();

    public PasswordResetTokenRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(PasswordResetToken resetToken) {
        String sql = "INSERT INTO password_reset_token (email, account_role, token, expires_at, used) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                resetToken.getEmail(),
                resetToken.getAccountRole(),
                resetToken.getToken(),
                Timestamp.valueOf(resetToken.getExpiresAt()),
                resetToken.isUsed());
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        String sql = "SELECT * FROM password_reset_token WHERE token = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, token));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public int markUsed(Long tokenId) {
        String sql = "UPDATE password_reset_token SET used = TRUE WHERE token_id = ?";
        return jdbcTemplate.update(sql, tokenId);
    }

    public void invalidateActiveTokensForEmail(String email, String accountRole) {
        String sql = "UPDATE password_reset_token SET used = TRUE WHERE email = ? AND account_role = ? AND used = FALSE";
        jdbcTemplate.update(sql, email, accountRole);
    }
}