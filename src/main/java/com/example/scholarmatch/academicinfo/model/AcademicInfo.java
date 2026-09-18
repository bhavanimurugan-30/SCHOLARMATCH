package com.example.scholarmatch.academicinfo.model;

public class AcademicInfo {

    private Long academicInfoId;
    private Long studentId;
    private String educationLevel;         // SCHOOL, DIPLOMA, UNDERGRADUATE, POSTGRADUATE, PHD
    private String courseName;
    private String institutionName;
    private String currentYearSemester;
    private Double qualifyingExamPercentage;
    private String specialization;
    private String universityBoard;
    private Integer admissionYear;
    private String modeOfStudy;            // REGULAR, DISTANCE, ONLINE
    private String rollNumber;
    private Double tenthPercentage;
    private Double twelfthPercentage;
    private Integer currentYear;
    private Integer currentSemester;

    public AcademicInfo() {
    }

    public Long getAcademicInfoId() {
        return academicInfoId;
    }

    public void setAcademicInfoId(Long academicInfoId) {
        this.academicInfoId = academicInfoId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

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