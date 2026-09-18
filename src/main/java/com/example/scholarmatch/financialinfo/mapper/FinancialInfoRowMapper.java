package com.example.scholarmatch.financialinfo.mapper;

import com.example.scholarmatch.financialinfo.model.FinancialInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FinancialInfoRowMapper implements RowMapper<FinancialInfo> {

    @Override
    public FinancialInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        FinancialInfo info = new FinancialInfo();
        info.setFinancialInfoId(rs.getLong("financial_info_id"));
        info.setStudentId(rs.getLong("student_id"));

        double income = rs.getDouble("annual_family_income");
        info.setAnnualFamilyIncome(rs.wasNull() ? null : income);

        info.setFatherOccupation(rs.getString("father_occupation"));
        info.setMotherOccupation(rs.getString("mother_occupation"));

        Object bplObj = rs.getObject("bpl_status");
        info.setBplStatus(bplObj == null ? null : rs.getBoolean("bpl_status"));

        info.setBankAccountNumber(rs.getString("bank_account_number"));
        info.setBankIfsc(rs.getString("bank_ifsc"));
        info.setBankName(rs.getString("bank_name"));

        return info;
    }
}