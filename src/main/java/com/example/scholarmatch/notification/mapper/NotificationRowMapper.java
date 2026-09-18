package com.example.scholarmatch.notification.mapper;

import com.example.scholarmatch.notification.model.Notification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationRowMapper implements RowMapper<Notification> {

    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
        Notification n = new Notification();
        n.setNotificationId(rs.getLong("notification_id"));
        n.setRecipientType(rs.getString("recipient_type"));

        long studentId = rs.getLong("student_id");
        n.setStudentId(rs.wasNull() ? null : studentId);

        long institutionId = rs.getLong("institution_id");
        n.setInstitutionId(rs.wasNull() ? null : institutionId);

        n.setNotificationType(rs.getString("notification_type"));

        long relatedScholarshipId = rs.getLong("related_scholarship_id");
        n.setRelatedScholarshipId(rs.wasNull() ? null : relatedScholarshipId);

        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setEmailSent(rs.getBoolean("is_email_sent"));

        if (rs.getTimestamp("created_at") != null) {
            n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return n;
    }
}