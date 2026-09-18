package com.example.scholarmatch.student.repository;

import com.example.scholarmatch.student.mapper.StudentRowMapper;
import com.example.scholarmatch.student.model.Student;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;
    private final StudentRowMapper rowMapper = new StudentRowMapper();

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Student save(Student student) {

        String sql = "INSERT INTO student " +
                "(full_name, date_of_birth, gender, mobile_number, email, password_hash, category, " +
                "state, district, aadhaar_number, permanent_address, religion, is_pwd, " +
                "disability_percentage, annual_income, marks_cgpa, account_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, student.getFullName());
            ps.setDate(2, java.sql.Date.valueOf(student.getDateOfBirth()));
            ps.setString(3, student.getGender());
            ps.setString(4, student.getMobileNumber());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getPasswordHash());
            ps.setString(7, student.getCategory());
            ps.setString(8, student.getState());
            ps.setString(9, student.getDistrict());
            ps.setString(10, student.getAadhaarNumber());
            ps.setString(11, student.getPermanentAddress());
            ps.setString(12, student.getReligion());
            ps.setBoolean(13, student.isPwd());
            if (student.getDisabilityPercentage() != null) {
                ps.setDouble(14, student.getDisabilityPercentage());
            } else {
                ps.setNull(14, Types.DECIMAL);
            }
            if (student.getAnnualIncome() != null) {
                ps.setDouble(15, student.getAnnualIncome());
            } else {
                ps.setNull(15, Types.DECIMAL);
            }
            if (student.getMarksCgpa() != null) {
                ps.setDouble(16, student.getMarksCgpa());
            } else {
                ps.setNull(16, Types.DECIMAL);
            }
            ps.setString(17, student.getAccountStatus() != null ? student.getAccountStatus() : "ACTIVE");
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<Student> findById(Long studentId) {
        String sql = "SELECT * FROM student WHERE student_id = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, studentId);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<Student> findByEmail(String email) {
        String sql = "SELECT * FROM student WHERE email = ?";
        try {
            Student student = jdbcTemplate.queryForObject(sql, rowMapper, email);
            return Optional.ofNullable(student);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Student> findAll() {
        String sql = "SELECT * FROM student ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM student WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean existsByMobileNumber(String mobileNumber) {
        String sql = "SELECT COUNT(*) FROM student WHERE mobile_number = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mobileNumber);
        return count != null && count > 0;
    }

    public int update(Long studentId, Student student) {
        String sql = "UPDATE student SET full_name = ?, date_of_birth = ?, gender = ?, mobile_number = ?, " +
                "email = ?, category = ?, state = ?, district = ?, aadhaar_number = ?, " +
                "permanent_address = ?, religion = ?, is_pwd = ?, disability_percentage = ?, " +
                "annual_income = ?, marks_cgpa = ?, profile_photo_url = ?, domicile = ?, income_category = ? " +
                "WHERE student_id = ?";

        return jdbcTemplate.update(sql,
                student.getFullName(),
                java.sql.Date.valueOf(student.getDateOfBirth()),
                student.getGender(),
                student.getMobileNumber(),
                student.getEmail(),
                student.getCategory(),
                student.getState(),
                student.getDistrict(),
                student.getAadhaarNumber(),
                student.getPermanentAddress(),
                student.getReligion(),
                student.isPwd(),
                student.getDisabilityPercentage(),
                student.getAnnualIncome(),
                student.getMarksCgpa(),
                student.getProfilePhotoUrl(),
                student.getDomicile(),
                student.getIncomeCategory(),
                studentId
        );
    }

    public int updateProfilePhoto(Long studentId, String photoUrl) {
        String sql = "UPDATE student SET profile_photo_url = ? WHERE student_id = ?";
        return jdbcTemplate.update(sql, photoUrl, studentId);
    }

    public int updateAccountStatus(Long studentId, String status) {
        String sql = "UPDATE student SET account_status = ? WHERE student_id = ?";
        return jdbcTemplate.update(sql, status, studentId);
    }

    public int deleteById(Long studentId) {
        String sql = "DELETE FROM student WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }

    public int updatePasswordHash(Long studentId, String passwordHash) {
        String sql = "UPDATE student SET password_hash = ? WHERE student_id = ?";
        return jdbcTemplate.update(sql, passwordHash, studentId);
    }
}