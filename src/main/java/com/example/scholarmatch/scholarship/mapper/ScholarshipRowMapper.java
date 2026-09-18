package com.example.scholarmatch.scholarship.mapper;

import com.example.scholarmatch.scholarship.model.Scholarship;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ScholarshipRowMapper implements RowMapper<Scholarship> {

    private final ObjectMapper objectMapper;

    public ScholarshipRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Scholarship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Scholarship s = new Scholarship();
        s.setScholarshipId(rs.getLong("scholarship_id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setSourceType(rs.getString("source_type"));
        try {
            s.setPrimaryCategory(rs.getString("primary_category"));
        } catch (SQLException ignored) {}
        s.setFundingBodyName(rs.getString("funding_body_name"));

        long providerInstitutionId = rs.getLong("provider_institution_id");
        s.setProviderInstitutionId(rs.wasNull() ? null : providerInstitutionId);

        double amount = rs.getDouble("amount");
        s.setAmount(rs.wasNull() ? null : amount);

        s.setApplicationMode(rs.getString("application_mode"));
        s.setOfficialApplicationLink(rs.getString("official_application_link"));

        s.setEligibleCategories(readJsonList(rs.getString("eligible_categories")));

        double minIncome = rs.getDouble("min_annual_income");
        s.setMinAnnualIncome(rs.wasNull() ? null : minIncome);

        double maxIncome = rs.getDouble("max_annual_income");
        s.setMaxAnnualIncome(rs.wasNull() ? null : maxIncome);

        s.setEligibleCourses(readJsonList(rs.getString("eligible_courses")));
        s.setEducationLevel(rs.getString("education_level"));

        double minMarks = rs.getDouble("min_marks_cgpa");
        s.setMinMarksCgpa(rs.wasNull() ? null : minMarks);

        s.setEligibleStates(readJsonList(rs.getString("eligible_states")));
        s.setEligibleSpecialStatus(readJsonList(rs.getString("eligible_special_status")));
        s.setRequiredDocuments(readJsonLongList(rs.getString("required_documents")));

        if (rs.getDate("deadline") != null) {
            s.setDeadline(rs.getDate("deadline").toLocalDate());
        }

        s.setApprovalStatus(rs.getString("approval_status"));
        s.setActive(rs.getBoolean("is_active"));

        long submittedByAdminId = rs.getLong("submitted_by_admin_id");
        s.setSubmittedByAdminId(rs.wasNull() ? null : submittedByAdminId);

        long submittedByInstitutionId = rs.getLong("submitted_by_institution_id");
        s.setSubmittedByInstitutionId(rs.wasNull() ? null : submittedByInstitutionId);

        long approvedByAdminId = rs.getLong("approved_by_admin_id");
        s.setApprovedByAdminId(rs.wasNull() ? null : approvedByAdminId);

        s.setRejectionReason(rs.getString("rejection_reason"));
        s.setViewCount(rs.getInt("view_count"));

        if (rs.getTimestamp("created_at") != null) {
            s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            s.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return s;
    }

    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private List<Long> readJsonLongList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}