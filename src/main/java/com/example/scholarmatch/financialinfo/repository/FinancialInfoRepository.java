package com.example.scholarmatch.financialinfo.repository;

import com.example.scholarmatch.financialinfo.mapper.FinancialInfoRowMapper;
import com.example.scholarmatch.financialinfo.model.FinancialInfo;
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
public class FinancialInfoRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FinancialInfoRowMapper rowMapper = new FinancialInfoRowMapper();

    public FinancialInfoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FinancialInfo save(FinancialInfo info) {
        String sql = "INSERT INTO student_financial_info " +
                "(student_id, annual_family_income, father_occupation, mother_occupation, bpl_status, " +
                "bank_account_number, bank_ifsc, bank_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, info.getStudentId());
            ps.setDouble(2, info.getAnnualFamilyIncome());
            ps.setString(3, info.getFatherOccupation());
            ps.setString(4, info.getMotherOccupation());
            if (info.getBplStatus() != null) {
                ps.setBoolean(5, info.getBplStatus());
            } else {
                ps.setNull(5, Types.BOOLEAN);
            }
            ps.setString(6, info.getBankAccountNumber());
            ps.setString(7, info.getBankIfsc());
            ps.setString(8, info.getBankName());
            return ps;
        }, keyHolder);

        Long generatedId = keyHolder.getKey().longValue();
        return findById(generatedId).orElseThrow();
    }

    public Optional<FinancialInfo> findById(Long financialInfoId) {
        String sql = "SELECT * FROM student_financial_info WHERE financial_info_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, financialInfoId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public Optional<FinancialInfo> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM student_financial_info WHERE student_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, studentId));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public boolean existsByStudentId(Long studentId) {
        String sql = "SELECT COUNT(*) FROM student_financial_info WHERE student_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, studentId);
        return count != null && count > 0;
    }

    public int updateByStudentId(Long studentId, FinancialInfo info) {
        String sql = "UPDATE student_financial_info SET annual_family_income = ?, father_occupation = ?, " +
                "mother_occupation = ?, bpl_status = ?, bank_account_number = ?, bank_ifsc = ?, " +
                "bank_name = ? WHERE student_id = ?";

        return jdbcTemplate.update(sql,
                info.getAnnualFamilyIncome(),
                info.getFatherOccupation(),
                info.getMotherOccupation(),
                info.getBplStatus(),
                info.getBankAccountNumber(),
                info.getBankIfsc(),
                info.getBankName(),
                studentId
        );
    }

    public int deleteByStudentId(Long studentId) {
        String sql = "DELETE FROM student_financial_info WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}