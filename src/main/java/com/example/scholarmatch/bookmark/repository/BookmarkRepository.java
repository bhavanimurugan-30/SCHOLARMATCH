package com.example.scholarmatch.bookmark.repository;

import com.example.scholarmatch.bookmark.mapper.BookmarkRowMapper;
import com.example.scholarmatch.bookmark.model.Bookmark;
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
public class BookmarkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final BookmarkRowMapper rowMapper = new BookmarkRowMapper();

    public BookmarkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Bookmark save(Bookmark bookmark) {

        if (exists(
                bookmark.getStudentId(),
                bookmark.getScholarshipId())) {

            String sql =
                    "UPDATE bookmark SET " +
                            "saved_at = NOW(), " +
                            "bookmarked_at = NOW(), " +
                            "status = IF(applied_at IS NOT NULL, " +
                            "'SAVED_AND_APPLIED', 'SAVED') " +
                            "WHERE student_id = ? " +
                            "AND scholarship_id = ?";

            jdbcTemplate.update(
                    sql,
                    bookmark.getStudentId(),
                    bookmark.getScholarshipId()
            );

            return findByStudentIdAndScholarshipId(
                    bookmark.getStudentId(),
                    bookmark.getScholarshipId()
            ).orElseThrow();
        }

        String sql =
                "INSERT INTO bookmark " +
                        "(student_id, scholarship_id, saved_at, bookmarked_at, status) " +
                        "VALUES (?, ?, NOW(), NOW(), 'SAVED')";

        KeyHolder keyHolder =
                new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps =
                    connection.prepareStatement(
                            sql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            ps.setLong(
                    1,
                    bookmark.getStudentId()
            );

            ps.setLong(
                    2,
                    bookmark.getScholarshipId()
            );

            return ps;

        }, keyHolder);

        Long generatedId =
                keyHolder.getKey().longValue();

        return findById(generatedId)
                .orElseThrow();
    }

    public Optional<Bookmark> findById(
            Long bookmarkId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE bookmark_id = ?";

        try {

            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            bookmarkId
                    )
            );

        } catch (EmptyResultDataAccessException ex) {

            return Optional.empty();
        }
    }

    public Optional<Bookmark> findByStudentIdAndScholarshipId(
            Long studentId,
            Long scholarshipId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ?";

        try {

            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            studentId,
                            scholarshipId
                    )
            );

        } catch (EmptyResultDataAccessException ex) {

            return Optional.empty();
        }
    }

    public List<Bookmark> findByStudentId(
            Long studentId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE student_id = ? " +
                        "ORDER BY bookmarked_at DESC";

        return jdbcTemplate.query(
                sql,
                rowMapper,
                studentId
        );
    }

    public List<Bookmark> findSavedByStudentId(
            Long studentId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND (saved_at IS NOT NULL " +
                        "OR status IN ('SAVED', 'SAVED_AND_APPLIED')) " +
                        "ORDER BY COALESCE(saved_at, bookmarked_at) DESC";

        return jdbcTemplate.query(
                sql,
                rowMapper,
                studentId
        );
    }

    public List<Bookmark> findAppliedByStudentId(
            Long studentId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND applied_at IS NOT NULL " +
                        "ORDER BY applied_at DESC";

        return jdbcTemplate.query(
                sql,
                rowMapper,
                studentId
        );
    }

    public boolean exists(
            Long studentId,
            Long scholarshipId) {

        String sql =
                "SELECT COUNT(*) FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ?";

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        studentId,
                        scholarshipId
                );

        return count != null && count > 0;
    }

    public boolean isSaved(
            Long studentId,
            Long scholarshipId) {

        String sql =
                "SELECT COUNT(*) FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ? " +
                        "AND (saved_at IS NOT NULL " +
                        "OR status IN ('SAVED', 'SAVED_AND_APPLIED'))";

        Integer count =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class,
                        studentId,
                        scholarshipId
                );

        return count != null && count > 0;
    }

    /**
     * Finds a student's application record
     * for a particular scholarship.
     *
     * Applied date remains immutable.
     */
    public Optional<Bookmark> findAppliedBookmark(
            Long studentId,
            Long scholarshipId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ? " +
                        "AND applied_at IS NOT NULL";

        try {

            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            studentId,
                            scholarshipId
                    )
            );

        } catch (EmptyResultDataAccessException ex) {

            return Optional.empty();
        }
    }

    /**
     * Finds all students who have currently saved
     * a particular scholarship.
     *
     * Used by DeadlineReminderService so that
     * deadline notifications are sent ONLY to
     * students who saved/bookmarked the scholarship.
     */
    public List<Bookmark> findSavedByScholarshipId(
            Long scholarshipId) {

        String sql =
                "SELECT * FROM bookmark " +
                        "WHERE scholarship_id = ? " +
                        "AND (saved_at IS NOT NULL " +
                        "OR status IN ('SAVED', 'SAVED_AND_APPLIED')) " +
                        "ORDER BY COALESCE(saved_at, bookmarked_at) DESC";

        return jdbcTemplate.query(
                sql,
                rowMapper,
                scholarshipId
        );
    }

    /**
     * Unsaving sets saved_at to null.
     *
     * If the student has already applied,
     * the application record remains untouched.
     *
     * If the student has not applied,
     * the bookmark row is deleted.
     */
    public int deleteByStudentIdAndScholarshipId(
            Long studentId,
            Long scholarshipId) {

        // If applied, preserve application.
        String updateSql =
                "UPDATE bookmark " +
                        "SET saved_at = NULL, " +
                        "status = 'APPLIED' " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ? " +
                        "AND applied_at IS NOT NULL";

        int updated =
                jdbcTemplate.update(
                        updateSql,
                        studentId,
                        scholarshipId
                );

        if (updated > 0) {
            return updated;
        }

        // If not applied, delete row.
        String deleteSql =
                "DELETE FROM bookmark " +
                        "WHERE student_id = ? " +
                        "AND scholarship_id = ? " +
                        "AND applied_at IS NULL";

        return jdbcTemplate.update(
                deleteSql,
                studentId,
                scholarshipId
        );
    }

    /**
     * Returns real application counts grouped
     * by scholarship for admin analytics.
     */
    public List<java.util.Map<String, Object>>
    findApplicationCountsByScholarship() {

        String sql =
                "SELECT scholarship_id, " +
                        "COUNT(*) AS application_count " +
                        "FROM bookmark " +
                        "WHERE applied_at IS NOT NULL " +
                        "GROUP BY scholarship_id";

        return jdbcTemplate.queryForList(sql);
    }

    /**
     * Marks a scholarship as applied.
     *
     * Applied date is strictly immutable once set.
     *
     * If the student has already applied,
     * nothing is changed.
     */
    public void markApplied(
            Long studentId,
            Long scholarshipId) {

        Optional<Bookmark> existing =
                findByStudentIdAndScholarshipId(
                        studentId,
                        scholarshipId
                );

        if (existing.isPresent()) {

            Bookmark b = existing.get();

            if (b.getAppliedAt() != null) {

                // Already applied.
                // Applied date remains immutable.
                return;
            }

            String sql =
                    "UPDATE bookmark " +
                            "SET status = IF(saved_at IS NOT NULL, " +
                            "'SAVED_AND_APPLIED', 'APPLIED'), " +
                            "applied_at = NOW() " +
                            "WHERE student_id = ? " +
                            "AND scholarship_id = ?";

            jdbcTemplate.update(
                    sql,
                    studentId,
                    scholarshipId
            );

        } else {

            String sql =
                    "INSERT INTO bookmark " +
                            "(student_id, scholarship_id, status, applied_at) " +
                            "VALUES (?, ?, 'APPLIED', NOW())";

            jdbcTemplate.update(
                    sql,
                    studentId,
                    scholarshipId
            );
        }
    }
}