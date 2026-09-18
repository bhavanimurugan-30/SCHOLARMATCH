package com.example.scholarmatch.scholarshipviewlog.mapper;

import com.example.scholarmatch.scholarshipviewlog.model.ScholarshipViewLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScholarshipViewLogRowMapper implements RowMapper<ScholarshipViewLog> {

    @Override
    public ScholarshipViewLog mapRow(ResultSet rs, int rowNum) throws SQLException {
        ScholarshipViewLog log = new ScholarshipViewLog();
        log.setViewId(rs.getLong("view_id"));
        log.setScholarshipId(rs.getLong("scholarship_id"));

        long studentId = rs.getLong("student_id");
        log.setStudentId(rs.wasNull() ? null : studentId);

        if (rs.getTimestamp("viewed_at") != null) {
            log.setViewedAt(rs.getTimestamp("viewed_at").toLocalDateTime());
        }

        return log;
    }
}