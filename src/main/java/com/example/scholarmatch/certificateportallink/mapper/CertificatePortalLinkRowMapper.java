package com.example.scholarmatch.certificateportallink.mapper;

import com.example.scholarmatch.certificateportallink.model.CertificatePortalLink;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CertificatePortalLinkRowMapper implements RowMapper<CertificatePortalLink> {

    @Override
    public CertificatePortalLink mapRow(ResultSet rs, int rowNum) throws SQLException {
        CertificatePortalLink link = new CertificatePortalLink();
        link.setPortalLinkId(rs.getLong("portal_link_id"));
        link.setCertificateTypeId(rs.getLong("certificate_type_id"));
        link.setState(rs.getString("state"));
        link.setPortalName(rs.getString("portal_name"));
        link.setPortalUrl(rs.getString("portal_url"));

        if (rs.getDate("last_verified_date") != null) {
            link.setLastVerifiedDate(rs.getDate("last_verified_date").toLocalDate());
        }

        return link;
    }
}