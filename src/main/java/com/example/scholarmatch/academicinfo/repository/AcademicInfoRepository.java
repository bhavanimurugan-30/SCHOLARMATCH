package com.example.scholarmatch.academicinfo.repository;

import com.example.scholarmatch.academicinfo.mapper.AcademicInfoRowMapper;
import com.example.scholarmatch.academicinfo.model.AcademicInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.Optional;

@Repository
public class AcademicInfoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AcademicInfoRowMapper rowMapper = new AcademicInfoRowMapper();

    public AcademicInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AcademicInfo save(AcademicInfo info) {
        String sql = "INSERT INTO student_academic_info " +
                "(student_id, education_level, course_name, institution_name, current_year_semester, " +
                "qualifying_exam_percentage, specialization, university_board, admission_year, " +
                "mode_of_study, roll_number, tenth_percentage, twelfth_percentage, current_year, current_semester) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, info.getStudentId());
            ps.setString(2, info.getEducationLevel());
            ps.setString(3, info.getCourseName());
            ps.setString(4, info.getInstitutionName());
            ps.setString(5, info.getCurrentYearSemester());
            ps.setDouble(6, info.getQualifyingExamPercentage());
            ps.setString(7, info.getSpecialization());
            ps.setString(8, info.getUniversityBoard());
            if (info.getAdmissionYear() != null) {
                ps.setInt(9, info.getAdmissionYear());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setString(10, info.getModeOfStudy());
            ps.setString(11, info.getRollNumber());
            if (info.getTenthPercentage() != null) ps.setDouble(12, info.getTenthPercentage()); else ps.setNull(12, Types.DECIMAL);
            if (info.getTwelfthPercentage() != null) ps.setDouble(13, info.getTwelfthPercentage()); else ps.setNull(13, Types.DECIMAL);
            if (info.getCurrentYear() != null) ps.setInt(14, info.getCurrentYear()); else ps.setNull(14, Types.INTEGER);
            if (info.getCurrentSemester() != null) ps.setInt(15, info.getCurrentSemester()); else ps.setNull(15, Types.INTEGER);
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<AcademicInfo> findById(Long academicInfoId) {
        String sql = "SELECT * FROM student_academic_info WHERE academic_info_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, academicInfoId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<AcademicInfo> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM student_academic_info WHERE student_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, studentId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public boolean existsByStudentId(Long studentId) {
        String sql = "SELECT COUNT(*) FROM student_academic_info WHERE student_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }

    public int updateByStudentId(Long studentId, AcademicInfo info) {
        String sql = "UPDATE student_academic_info SET education_level = ?, course_name = ?, " +
                "institution_name = ?, current_year_semester = ?, qualifying_exam_percentage = ?, " +
                "specialization = ?, university_board = ?, admission_year = ?, mode_of_study = ?, " +
                "roll_number = ?, tenth_percentage = ?, twelfth_percentage = ?, current_year = ?, current_semester = ? WHERE student_id = ?";

        return jdbcTemplate.update(sql,
                info.getEducationLevel(),
                info.getCourseName(),
                info.getInstitutionName(),
                info.getCurrentYearSemester(),
                info.getQualifyingExamPercentage(),
                info.getSpecialization(),
                info.getUniversityBoard(),
                info.getAdmissionYear(),
                info.getModeOfStudy(),
                info.getRollNumber(),
                info.getTenthPercentage(),
                info.getTwelfthPercentage(),
                info.getCurrentYear(),
                info.getCurrentSemester(),
                studentId
        );
    }

    public int deleteByStudentId(Long studentId) {
        String sql = "DELETE FROM student_academic_info WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}