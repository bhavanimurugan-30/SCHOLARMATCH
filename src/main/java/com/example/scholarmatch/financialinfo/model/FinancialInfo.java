package com.example.scholarmatch.financialinfo.model;

public class FinancialInfo {

    private Long financialInfoId;
    private Long studentId;
    private Double annualFamilyIncome;
    private String fatherOccupation;
    private String motherOccupation;
    private Boolean bplStatus;
    private String bankAccountNumber;
    private String bankIfsc;
    private String bankName;

    public FinancialInfo() {
    }

    public Long getFinancialInfoId() {
        return financialInfoId;
    }

    public void setFinancialInfoId(Long financialInfoId) {
        this.financialInfoId = financialInfoId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getAnnualFamilyIncome() {
        return annualFamilyIncome;
    }

    public void setAnnualFamilyIncome(Double annualFamilyIncome) {
        this.annualFamilyIncome = annualFamilyIncome;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public Boolean getBplStatus() {
        return bplStatus;
    }

    public void setBplStatus(Boolean bplStatus) {
        this.bplStatus = bplStatus;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankIfsc() {
        return bankIfsc;
    }

    public void setBankIfsc(String bankIfsc) {
        this.bankIfsc = bankIfsc;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}