package com.example.scholarmatch.specialstatus.repository;

import com.example.scholarmatch.specialstatus.mapper.SpecialStatusRowMapper;
import com.example.scholarmatch.specialstatus.model.SpecialStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class SpecialStatusRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SpecialStatusRowMapper rowMapper = new SpecialStatusRowMapper();

    public SpecialStatusRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SpecialStatus save(SpecialStatus status) {

        String sql = "INSERT INTO student_special_status " +
                "(student_id, is_single_girl_child, is_orphan_single_parent, " +
                "is_ex_servicemen_dependent, is_sports_quota, is_minority_community, " +
                "is_first_graduate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {

            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setLong(1, status.getStudentId());
            ps.setBoolean(2, status.isSingleGirlChild());
            ps.setBoolean(3, status.isOrphanSingleParent());
            ps.setBoolean(4, status.isExServicemenDependent());
            ps.setBoolean(5, status.isSportsQuota());
            ps.setBoolean(6, status.isMinorityCommunity());
            ps.setBoolean(7, status.isFirstGraduate());

            return ps;

        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();

        return findById(generatedId).orElseThrow();
    }

    public Optional<SpecialStatus> findById(Long specialStatusId) {

        String sql = "SELECT * FROM student_special_status " +
                "WHERE special_status_id = ?";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            specialStatusId
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<SpecialStatus> findByStudentId(Long studentId) {

        String sql = "SELECT * FROM student_special_status " +
                "WHERE student_id = ?";

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            sql,
                            rowMapper,
                            studentId
                    )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public boolean existsByStudentId(Long studentId) {

        String sql = "SELECT COUNT(*) FROM student_special_status " +
                "WHERE student_id = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                studentId
        );

        return count != null && count > 0;
    }

    public int updateByStudentId(Long studentId, SpecialStatus status) {

        String sql = "UPDATE student_special_status SET " +
                "is_single_girl_child = ?, " +
                "is_orphan_single_parent = ?, " +
                "is_ex_servicemen_dependent = ?, " +
                "is_sports_quota = ?, " +
                "is_minority_community = ?, " +
                "is_first_graduate = ? " +
                "WHERE student_id = ?";

        return jdbcTemplate.update(
                sql,
                status.isSingleGirlChild(),
                status.isOrphanSingleParent(),
                status.isExServicemenDependent(),
                status.isSportsQuota(),
                status.isMinorityCommunity(),
                status.isFirstGraduate(),
                studentId
        );
    }

    public int deleteByStudentId(Long studentId) {

        String sql = "DELETE FROM student_special_status " +
                "WHERE student_id = ?";

        return jdbcTemplate.update(sql, studentId);
    }
}