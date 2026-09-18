package com.example.scholarmatch.scholarship.repository;

import com.example.scholarmatch.scholarship.mapper.ScholarshipRowMapper;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class ScholarshipRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final ScholarshipRowMapper rowMapper;

    public ScholarshipRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.rowMapper = new ScholarshipRowMapper(objectMapper);
    }

    public Scholarship save(Scholarship s) {
        String sql = "INSERT INTO scholarship " +
                "(title, description, source_type, primary_category, funding_body_name, provider_institution_id, amount, " +
                "application_mode, official_application_link, eligible_categories, min_annual_income, " +
                "max_annual_income, eligible_courses, education_level, min_marks_cgpa, eligible_states, " +
                "eligible_special_status, required_documents, deadline, approval_status, is_active, submitted_by_admin_id, " +
                "submitted_by_institution_id, approved_by_admin_id, rejection_reason, view_count) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, s.getTitle());
            ps.setString(2, s.getDescription());
            ps.setString(3, s.getSourceType());
            ps.setString(4, s.getPrimaryCategory());
            ps.setString(5, s.getFundingBodyName());
            setNullableLong(ps, 6, s.getProviderInstitutionId());
            setNullableDouble(ps, 7, s.getAmount());
            ps.setString(8, s.getApplicationMode() != null ? s.getApplicationMode() : "EXTERNAL_REDIRECT");
            ps.setString(9, s.getOfficialApplicationLink());
            ps.setString(10, writeJson(s.getEligibleCategories()));
            setNullableDouble(ps, 11, s.getMinAnnualIncome());
            setNullableDouble(ps, 12, s.getMaxAnnualIncome());
            ps.setString(13, writeJson(s.getEligibleCourses()));
            ps.setString(14, s.getEducationLevel() != null ? s.getEducationLevel() : "ANY");
            setNullableDouble(ps, 15, s.getMinMarksCgpa());
            ps.setString(16, writeJson(s.getEligibleStates()));
            ps.setString(17, writeJson(s.getEligibleSpecialStatus()));
            ps.setString(18, writeJsonLong(s.getRequiredDocuments()));
            ps.setDate(19, Date.valueOf(s.getDeadline()));
            ps.setString(20, s.getApprovalStatus() != null ? s.getApprovalStatus() : "APPROVED");
            ps.setBoolean(21, s.isActive());
            setNullableLong(ps, 22, s.getSubmittedByAdminId());
            setNullableLong(ps, 23, s.getSubmittedByInstitutionId());
            setNullableLong(ps, 24, s.getApprovedByAdminId());
            ps.setString(25, s.getRejectionReason());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<Scholarship> findById(Long scholarshipId) {
        String sql = "SELECT * FROM scholarship WHERE scholarship_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, scholarshipId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Scholarship> findAll() {
        String sql = "SELECT * FROM scholarship ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }
    public List<Scholarship> findActiveApprovedWithDeadlineWithinDays(int days) {
        String sql = "SELECT * FROM scholarship WHERE approval_status = 'APPROVED' AND is_active = TRUE " +
                "AND deadline BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY deadline ASC";
        return jdbcTemplate.query(sql, rowMapper, days);
    }

    /**
     * Active + approved non-expired scholarships, for the student-facing views.
     */
    public List<Scholarship> findActiveApproved() {
        String sql = "SELECT * FROM scholarship WHERE approval_status = 'APPROVED' AND is_active = TRUE AND deadline >= CURDATE() " +
                "ORDER BY deadline ASC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public List<Scholarship> findByPrimaryCategory(String primaryCategory) {
        String sql = "SELECT * FROM scholarship WHERE primary_category = ? AND approval_status = 'APPROVED' AND is_active = TRUE AND deadline >= CURDATE() ORDER BY deadline ASC";
        return jdbcTemplate.query(sql, rowMapper, primaryCategory);
    }

    public List<Scholarship> findByApprovalStatus(String status) {
        String sql = "SELECT * FROM scholarship WHERE approval_status = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, rowMapper, status);
    }

    public List<Scholarship> findBySourceType(String sourceType) {
        String sql = "SELECT * FROM scholarship WHERE source_type = ? ORDER BY deadline ASC";
        return jdbcTemplate.query(sql, rowMapper, sourceType);
    }

    public List<Scholarship> findByProviderInstitutionId(Long institutionId) {
        String sql = "SELECT * FROM scholarship WHERE provider_institution_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, institutionId);
    }

    public int update(Long scholarshipId, Scholarship s) {
        String sql = "UPDATE scholarship SET title = ?, description = ?, primary_category = ?, funding_body_name = ?, amount = ?, " +
                "application_mode = ?, official_application_link = ?, eligible_categories = ?, " +
                "min_annual_income = ?, max_annual_income = ?, eligible_courses = ?, education_level = ?, " +
                "min_marks_cgpa = ?, eligible_states = ?, eligible_special_status = ?, required_documents = ?, deadline = ? " +
                "WHERE scholarship_id = ?";

        return jdbcTemplate.update(sql,
                s.getTitle(),
                s.getDescription(),
                s.getPrimaryCategory(),
                s.getFundingBodyName(),
                s.getAmount(),
                s.getApplicationMode(),
                s.getOfficialApplicationLink(),
                writeJson(s.getEligibleCategories()),
                s.getMinAnnualIncome(),
                s.getMaxAnnualIncome(),
                writeJson(s.getEligibleCourses()),
                s.getEducationLevel(),
                s.getMinMarksCgpa(),
                writeJson(s.getEligibleStates()),
                writeJson(s.getEligibleSpecialStatus()),
                writeJsonLong(s.getRequiredDocuments()),
                Date.valueOf(s.getDeadline()),
                scholarshipId
        );
    }

    public int updateApprovalStatus(Long scholarshipId, String status, Long approvedByAdminId, String rejectionReason) {
        String sql = "UPDATE scholarship SET approval_status = ?, approved_by_admin_id = ?, rejection_reason = ? " +
                "WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, status, approvedByAdminId, rejectionReason, scholarshipId);
    }

    public int updateActiveStatus(Long scholarshipId, boolean active) {
        String sql = "UPDATE scholarship SET is_active = ? WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, active, scholarshipId);
    }

    public int incrementViewCount(Long scholarshipId) {
        String sql = "UPDATE scholarship SET view_count = view_count + 1 WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, scholarshipId);
    }

    public int deleteById(Long scholarshipId) {
        String sql = "DELETE FROM scholarship WHERE scholarship_id = ?";
        return jdbcTemplate.update(sql, scholarshipId);
    }

    /**
     * Server-side search/filter/sort over actual scholarship data.
     * Only APPROVED + active scholarships are considered. When studentId is
     * given, LEFT JOINs the existing scholarship_match_score table so
     * matchPercentage filtering/sorting is also DB-driven, not client-side.
     */
    public List<Scholarship> search(com.example.scholarmatch.scholarship.dto.ScholarshipSearchCriteria c) {
        StringBuilder sql = new StringBuilder(
                "SELECT s.*" + (c.getStudentId() != null ? ", ms.match_percentage AS match_pct" : "") +
                        " FROM scholarship s");

        if (c.getStudentId() != null) {
            sql.append(" LEFT JOIN scholarship_match_score ms ON ms.scholarship_id = s.scholarship_id AND ms.student_id = ?");
        }

        sql.append(" WHERE s.approval_status = 'APPROVED' AND s.is_active = TRUE");

        List<Object> params = new java.util.ArrayList<>();
        if (c.getStudentId() != null) params.add(c.getStudentId());

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            sql.append(" AND (LOWER(s.title) LIKE ? OR LOWER(s.description) LIKE ? OR LOWER(s.funding_body_name) LIKE ?)");
            String like = "%" + c.getKeyword().toLowerCase() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (c.getCategory() != null && !c.getCategory().isBlank()) {
            sql.append(" AND (LOWER(s.eligible_categories) LIKE ? OR s.eligible_categories = '[]' OR s.eligible_categories IS NULL)");
            params.add("%\"" + c.getCategory().toLowerCase() + "\"%");
        }
        if (c.getState() != null && !c.getState().isBlank()) {
            sql.append(" AND (LOWER(s.eligible_states) LIKE ? OR s.eligible_states = '[]' OR s.eligible_states IS NULL)");
            params.add("%" + c.getState().toLowerCase() + "%");
        }
        if (c.getCourse() != null && !c.getCourse().isBlank()) {
            sql.append(" AND (LOWER(s.eligible_courses) LIKE ? OR s.eligible_courses = '[]' OR s.eligible_courses IS NULL)");
            params.add("%" + c.getCourse().toLowerCase() + "%");
        }
        if (c.getIncome() != null) {
            sql.append(" AND (s.min_annual_income IS NULL OR s.min_annual_income <= ?)");
            params.add(c.getIncome());
            sql.append(" AND (s.max_annual_income IS NULL OR s.max_annual_income >= ?)");
            params.add(c.getIncome());
        }
        if (c.getSourceType() != null && !c.getSourceType().isBlank()) {
            sql.append(" AND s.source_type = ?");
            params.add(c.getSourceType());
        }
        if (c.getMinAmount() != null) {
            sql.append(" AND s.amount >= ?");
            params.add(c.getMinAmount());
        }
        if (c.getMaxAmount() != null) {
            sql.append(" AND s.amount <= ?");
            params.add(c.getMaxAmount());
        }
        if (c.getDeadlineBefore() != null) {
            sql.append(" AND s.deadline <= ?");
            params.add(java.sql.Date.valueOf(c.getDeadlineBefore()));
        }
        if (c.getMinMatchPercentage() != null && c.getStudentId() != null) {
            sql.append(" AND ms.match_percentage >= ?");
            params.add(c.getMinMatchPercentage());
        }

        String sortBy = c.getSortBy() == null ? "deadline" : c.getSortBy();
        String dir = "desc".equalsIgnoreCase(c.getSortDir()) ? "DESC" : "ASC";
        switch (sortBy) {
            case "amount": sql.append(" ORDER BY s.amount ").append(dir); break;
            case "match": sql.append(" ORDER BY match_pct ").append(dir).append(" IS NULL, match_pct ").append(dir); break;
            default: sql.append(" ORDER BY s.deadline ").append(dir);
        }

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            Scholarship s = rowMapper.mapRow(rs, rowNum);
            if (c.getStudentId() != null) {
                double pct = rs.getDouble("match_pct");
                if (!rs.wasNull()) s.setMatchPercentage(pct);
            }
            return s;
        }, params.toArray());
    }

    private String writeJson(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }

    private String writeJsonLong(List<Long> list) {
        try {
            return objectMapper.writeValueAsString(list != null ? list : List.of());
        } catch (Exception e) {
            return "[]";
        }
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws java.sql.SQLException {
        if (value != null) {
            ps.setLong(index, value);
        } else {
            ps.setNull(index, Types.BIGINT);
        }
    }

    private void setNullableDouble(PreparedStatement ps, int index, Double value) throws java.sql.SQLException {
        if (value != null) {
            ps.setDouble(index, value);
        } else {
            ps.setNull(index, Types.DECIMAL);
        }
    }
}