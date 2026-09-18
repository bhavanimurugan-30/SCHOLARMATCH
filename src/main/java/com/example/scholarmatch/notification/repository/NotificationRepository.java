package com.example.scholarmatch.notification.repository;

import com.example.scholarmatch.notification.mapper.NotificationRowMapper;
import com.example.scholarmatch.notification.model.Notification;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NotificationRowMapper rowMapper = new NotificationRowMapper();

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Notification save(Notification n) {

        String sql = "INSERT INTO notification " +
                "(recipient_type, student_id, institution_id, notification_type, " +
                "related_scholarship_id, title, message, is_read, is_email_sent) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, n.getRecipientType());
            setNullableLong(ps, 2, n.getStudentId());
            setNullableLong(ps, 3, n.getInstitutionId());
            ps.setString(4, n.getNotificationType());
            setNullableLong(ps, 5, n.getRelatedScholarshipId());
            ps.setString(6, n.getTitle());
            ps.setString(7, n.getMessage());
            ps.setBoolean(8, n.isRead());
            ps.setBoolean(9, n.isEmailSent());

            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();

        return findById(generatedId).orElseThrow();
    }

    public Optional<Notification> findById(Long notificationId) {

        String sql =
                "SELECT * FROM notification WHERE notification_id = ?";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            notificationId
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Notification> findByStudentId(Long studentId) {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE student_id = ? " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    public List<Notification> findByInstitutionId(Long institutionId) {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE institution_id = ? " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }

    /**
     * ADMIN notifications are shared across the admin notification feed.
     * No admin_id column is required.
     */
    public List<Notification> findForAdmin() {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE recipient_type = 'ADMIN' " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Notification> findUnreadForAdmin() {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE recipient_type = 'ADMIN' " +
                        "AND is_read = FALSE " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper);
    }

    public int countUnreadForAdmin() {

        String sql =
                "SELECT COUNT(*) FROM notification " +
                        "WHERE recipient_type = 'ADMIN' " +
                        "AND is_read = FALSE";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class
        );

        return count == null ? 0 : count;
    }

    public int markAllAsReadForAdmin() {

        String sql =
                "UPDATE notification " +
                        "SET is_read = TRUE " +
                        "WHERE recipient_type = 'ADMIN' " +
                        "AND is_read = FALSE";

        return jdbcTemplate.update(sql);
    }

    public List<Notification> findUnreadByStudentId(Long studentId) {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE student_id = ? " +
                        "AND is_read = FALSE " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper, studentId);
    }

    public List<Notification> findUnreadByInstitutionId(Long institutionId) {

        String sql =
                "SELECT * FROM notification " +
                        "WHERE institution_id = ? " +
                        "AND is_read = FALSE " +
                        "ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }

    public int markAsRead(Long notificationId) {

        String sql =
                "UPDATE notification " +
                        "SET is_read = TRUE " +
                        "WHERE notification_id = ?";

        return jdbcTemplate.update(sql, notificationId);
    }

    public int markAllAsReadByStudentId(Long studentId) {

        String sql =
                "UPDATE notification " +
                        "SET is_read = TRUE " +
                        "WHERE student_id = ? " +
                        "AND is_read = FALSE";

        return jdbcTemplate.update(sql, studentId);
    }

    public int markAllAsReadByInstitutionId(Long institutionId) {

        String sql =
                "UPDATE notification " +
                        "SET is_read = TRUE " +
                        "WHERE institution_id = ? " +
                        "AND is_read = FALSE";

        return jdbcTemplate.update(sql, institutionId);
    }

    public int countUnreadByStudentId(Long studentId) {

        String sql =
                "SELECT COUNT(*) FROM notification " +
                        "WHERE student_id = ? " +
                        "AND is_read = FALSE";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                studentId
        );

        return count == null ? 0 : count;
    }

    public int countUnreadByInstitutionId(Long institutionId) {

        String sql =
                "SELECT COUNT(*) FROM notification " +
                        "WHERE institution_id = ? " +
                        "AND is_read = FALSE";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                institutionId
        );

        return count == null ? 0 : count;
    }

    public int markEmailSent(Long notificationId) {

        String sql =
                "UPDATE notification " +
                        "SET is_email_sent = TRUE " +
                        "WHERE notification_id = ?";

        return jdbcTemplate.update(sql, notificationId);
    }

    public int deleteById(Long notificationId) {

        String sql =
                "DELETE FROM notification " +
                        "WHERE notification_id = ?";

        return jdbcTemplate.update(sql, notificationId);
    }

    public boolean existsByStudentScholarshipAndType(
            Long studentId,
            Long scholarshipId,
            String notificationType) {

        String sql =
                "SELECT COUNT(*) FROM notification " +
                        "WHERE student_id = ? " +
                        "AND related_scholarship_id = ? " +
                        "AND notification_type = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                studentId,
                scholarshipId,
                notificationType
        );

        return count != null && count > 0;
    }

    private void setNullableLong(
            PreparedStatement ps,
            int index,
            Long value) throws java.sql.SQLException {

        if (value != null) {
            ps.setLong(index, value);
        } else {
            ps.setNull(index, Types.BIGINT);
        }
    }
}