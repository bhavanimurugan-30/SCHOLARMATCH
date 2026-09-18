package com.example.scholarmatch.bookmark.mapper;

import com.example.scholarmatch.bookmark.model.Bookmark;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookmarkRowMapper implements RowMapper<Bookmark> {

    @Override
    public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
        Bookmark b = new Bookmark();
        b.setBookmarkId(rs.getLong("bookmark_id"));
        b.setStudentId(rs.getLong("student_id"));
        b.setScholarshipId(rs.getLong("scholarship_id"));

        if (rs.getTimestamp("bookmarked_at") != null) {
            b.setBookmarkedAt(rs.getTimestamp("bookmarked_at").toLocalDateTime());
        }

        b.setStatus(rs.getString("status"));
        try {
            if (rs.getTimestamp("saved_at") != null) {
                b.setSavedAt(rs.getTimestamp("saved_at").toLocalDateTime());
            }
        } catch (SQLException ignored) {}
        if (rs.getTimestamp("applied_at") != null) {
            b.setAppliedAt(rs.getTimestamp("applied_at").toLocalDateTime());
        }

        return b;
    }
}