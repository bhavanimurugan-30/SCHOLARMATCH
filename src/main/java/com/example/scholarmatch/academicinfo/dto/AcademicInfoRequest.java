package com.example.scholarmatch.academicinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AcademicInfoRequest {

    @NotBlank(message = "Education level is required")
    @Pattern(regexp = "SCHOOL|DIPLOMA|UNDERGRADUATE|POSTGRADUATE|PHD", message = "Invalid education level")
    private String educationLevel;

    @NotBlank(message = "Course name is required")
    private String courseName;

    @NotBlank(message = "Institution name is required")
    private String institutionName;

    @NotBlank(message = "Current year/semester is required")
    private String currentYearSemester;

    @NotNull(message = "Qualifying exam percentage/CGPA is required")
    private Double qualifyingExamPercentage;

    private String specialization;
    private String universityBoard;
    private Integer admissionYear;

    @Pattern(regexp = "^$|REGULAR|DISTANCE|ONLINE", message = "Invalid mode of study")
    private String modeOfStudy;

    private String rollNumber;

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getCurrentYearSemester() {
        return currentYearSemester;
    }

    public void setCurrentYearSemester(String currentYearSemester) {
        this.currentYearSemester = currentYearSemester;
    }

    public Double getQualifyingExamPercentage() {
        return qualifyingExamPercentage;
    }

    public void setQualifyingExamPercentage(Double qualifyingExamPercentage) {
        this.qualifyingExamPercentage = qualifyingExamPercentage;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getUniversityBoard() {
        return universityBoard;
    }

    public void setUniversityBoard(String universityBoard) {
        this.universityBoard = universityBoard;
    }

    public Integer getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(Integer admissionYear) {
        this.admissionYear = admissionYear;
    }

    public String getModeOfStudy() {
        return modeOfStudy;
    }

    public void setModeOfStudy(String modeOfStudy) {
        this.modeOfStudy = modeOfStudy;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    private Double tenthPercentage;
    private Double twelfthPercentage;
    private Integer currentYear;
    private Integer currentSemester;

    public Double getTenthPercentage() {
        return tenthPercentage;
    }

    public void setTenthPercentage(Double tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }

    public Double getTwelfthPercentage() {
        return twelfthPercentage;
    }

    public void setTwelfthPercentage(Double twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }
}