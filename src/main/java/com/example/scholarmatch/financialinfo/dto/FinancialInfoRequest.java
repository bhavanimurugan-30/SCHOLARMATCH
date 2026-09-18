package com.example.scholarmatch.financialinfo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class FinancialInfoRequest {

    @NotNull(message = "Annual family income is required")
    @PositiveOrZero(message = "Annual family income cannot be negative")
    private Double annualFamilyIncome;

    private String fatherOccupation;
    private String motherOccupation;
    private Boolean bplStatus;
    private String bankAccountNumber;
    private String bankIfsc;
    private String bankName;

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