package com.example.scholarmatch.academicinfo.mapper;

import com.example.scholarmatch.academicinfo.model.AcademicInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AcademicInfoRowMapper implements RowMapper<AcademicInfo> {

    @Override
    public AcademicInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        AcademicInfo info = new AcademicInfo();
        info.setAcademicInfoId(rs.getLong("academic_info_id"));
        info.setStudentId(rs.getLong("student_id"));
        info.setEducationLevel(rs.getString("education_level"));
        info.setCourseName(rs.getString("course_name"));
        info.setInstitutionName(rs.getString("institution_name"));
        info.setCurrentYearSemester(rs.getString("current_year_semester"));

        double pct = rs.getDouble("qualifying_exam_percentage");
        info.setQualifyingExamPercentage(rs.wasNull() ? null : pct);

        info.setSpecialization(rs.getString("specialization"));
        info.setUniversityBoard(rs.getString("university_board"));

        int admissionYear = rs.getInt("admission_year");
        info.setAdmissionYear(rs.wasNull() ? null : admissionYear);

        info.setModeOfStudy(rs.getString("mode_of_study"));
        info.setRollNumber(rs.getString("roll_number"));

        try {
            double t = rs.getDouble("tenth_percentage");
            info.setTenthPercentage(rs.wasNull() ? null : t);
        } catch (SQLException ignored) {}
        try {
            double tw = rs.getDouble("twelfth_percentage");
            info.setTwelfthPercentage(rs.wasNull() ? null : tw);
        } catch (SQLException ignored) {}
        try {
            int cy = rs.getInt("current_year");
            info.setCurrentYear(rs.wasNull() ? null : cy);
        } catch (SQLException ignored) {}
        try {
            int cs = rs.getInt("current_semester");
            info.setCurrentSemester(rs.wasNull() ? null : cs);
        } catch (SQLException ignored) {}

        return info;
    }
}