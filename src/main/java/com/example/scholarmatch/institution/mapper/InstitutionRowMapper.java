package com.example.scholarmatch.institution.mapper;

import com.example.scholarmatch.institution.model.Institution;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionRowMapper implements RowMapper<Institution> {

    @Override
    public Institution mapRow(ResultSet rs, int rowNum) throws SQLException {
        Institution institution = new Institution();
        institution.setInstitutionId(rs.getLong("institution_id"));
        institution.setInstitutionName(rs.getString("institution_name"));
        institution.setInstitutionType(rs.getString("institution_type"));
        institution.setEmail(rs.getString("email"));
        institution.setPasswordHash(rs.getString("password_hash"));
        institution.setWebsiteUrl(rs.getString("website_url"));
        institution.setState(rs.getString("state"));
        institution.setDistrict(rs.getString("district"));
        institution.setPanNumber(rs.getString("pan_number"));
        institution.setRegistrationNumber(rs.getString("registration_number"));
        institution.setVerificationStatus(rs.getString("verification_status"));

        if (rs.getTimestamp("created_at") != null) {
            institution.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            institution.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return institution;
    }
}