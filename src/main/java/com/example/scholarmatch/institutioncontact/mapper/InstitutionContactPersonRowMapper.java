package com.example.scholarmatch.institutioncontact.mapper;

import com.example.scholarmatch.institutioncontact.model.InstitutionContactPerson;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstitutionContactPersonRowMapper implements RowMapper<InstitutionContactPerson> {

    @Override
    public InstitutionContactPerson mapRow(ResultSet rs, int rowNum) throws SQLException {
        InstitutionContactPerson contact = new InstitutionContactPerson();
        contact.setContactPersonId(rs.getLong("contact_person_id"));
        contact.setInstitutionId(rs.getLong("institution_id"));
        contact.setFullName(rs.getString("full_name"));
        contact.setDesignation(rs.getString("designation"));
        contact.setPhone(rs.getString("phone"));
        contact.setEmail(rs.getString("email"));
        return contact;
    }
}