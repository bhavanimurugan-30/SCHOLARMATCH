package com.example.scholarmatch.student.mapper;

import com.example.scholarmatch.student.model.Student;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRowMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getLong("student_id"));
        student.setFullName(rs.getString("full_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setGender(rs.getString("gender"));
        student.setMobileNumber(rs.getString("mobile_number"));
        student.setEmail(rs.getString("email"));
        student.setPasswordHash(rs.getString("password_hash"));
        student.setCategory(rs.getString("category"));
        student.setState(rs.getString("state"));
        student.setDistrict(rs.getString("district"));
        student.setAadhaarNumber(rs.getString("aadhaar_number"));
        student.setPermanentAddress(rs.getString("permanent_address"));
        student.setReligion(rs.getString("religion"));
        student.setPwd(rs.getBoolean("is_pwd"));

        double disabilityPct = rs.getDouble("disability_percentage");
        student.setDisabilityPercentage(rs.wasNull() ? null : disabilityPct);
        double annualIncome = rs.getDouble("annual_income");
        student.setAnnualIncome(rs.wasNull() ? null : annualIncome);

        double marksCgpa = rs.getDouble("marks_cgpa");
        student.setMarksCgpa(rs.wasNull() ? null : marksCgpa);
        student.setAccountStatus(rs.getString("account_status"));

        try { student.setProfilePhotoUrl(rs.getString("profile_photo_url")); } catch (SQLException ignored) {}
        try { student.setDomicile(rs.getString("domicile")); } catch (SQLException ignored) {}
        try { student.setIncomeCategory(rs.getString("income_category")); } catch (SQLException ignored) {}

        if (rs.getTimestamp("created_at") != null) {
            student.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            student.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }

        return student;
    }
}