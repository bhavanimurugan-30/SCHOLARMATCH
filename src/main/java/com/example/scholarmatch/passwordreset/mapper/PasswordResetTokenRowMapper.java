package com.example.scholarmatch.passwordreset.mapper;

import com.example.scholarmatch.passwordreset.model.PasswordResetToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordResetTokenRowMapper implements RowMapper<PasswordResetToken> {

    @Override
    public PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        PasswordResetToken t = new PasswordResetToken();
        t.setTokenId(rs.getLong("token_id"));
        t.setEmail(rs.getString("email"));
        t.setAccountRole(rs.getString("account_role"));
        t.setToken(rs.getString("token"));
        t.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
        t.setUsed(rs.getBoolean("used"));
        if (rs.getTimestamp("created_at") != null) {
            t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return t;
    }
}