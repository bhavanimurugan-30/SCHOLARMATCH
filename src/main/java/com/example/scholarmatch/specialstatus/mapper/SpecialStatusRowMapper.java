package com.example.scholarmatch.specialstatus.mapper;

import com.example.scholarmatch.specialstatus.model.SpecialStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SpecialStatusRowMapper implements RowMapper<SpecialStatus> {

    @Override
    public SpecialStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpecialStatus status = new SpecialStatus();
        status.setSpecialStatusId(rs.getLong("special_status_id"));
        status.setStudentId(rs.getLong("student_id"));
        status.setSingleGirlChild(rs.getBoolean("is_single_girl_child"));
        status.setOrphanSingleParent(rs.getBoolean("is_orphan_single_parent"));
        status.setExServicemenDependent(rs.getBoolean("is_ex_servicemen_dependent"));
        status.setSportsQuota(rs.getBoolean("is_sports_quota"));
        status.setMinorityCommunity(rs.getBoolean("is_minority_community"));
        status.setFirstGraduate(rs.getBoolean("is_first_graduate"));
        return status;
    }
}