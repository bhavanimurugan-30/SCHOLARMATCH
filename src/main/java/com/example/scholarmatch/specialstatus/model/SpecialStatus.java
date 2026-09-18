package com.example.scholarmatch.specialstatus.model;

public class SpecialStatus {

    private Long specialStatusId;
    private Long studentId;
    private boolean singleGirlChild;
    private boolean orphanSingleParent;
    private boolean exServicemenDependent;
    private boolean sportsQuota;
    private boolean minorityCommunity;
    private boolean firstGraduate;

    public SpecialStatus() {
    }

    public Long getSpecialStatusId() {
        return specialStatusId;
    }

    public void setSpecialStatusId(Long specialStatusId) {
        this.specialStatusId = specialStatusId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public boolean isSingleGirlChild() {
        return singleGirlChild;
    }

    public void setSingleGirlChild(boolean singleGirlChild) {
        this.singleGirlChild = singleGirlChild;
    }

    public boolean isOrphanSingleParent() {
        return orphanSingleParent;
    }

    public void setOrphanSingleParent(boolean orphanSingleParent) {
        this.orphanSingleParent = orphanSingleParent;
    }

    public boolean isExServicemenDependent() {
        return exServicemenDependent;
    }

    public void setExServicemenDependent(boolean exServicemenDependent) {
        this.exServicemenDependent = exServicemenDependent;
    }

    public boolean isSportsQuota() {
        return sportsQuota;
    }

    public void setSportsQuota(boolean sportsQuota) {
        this.sportsQuota = sportsQuota;
    }

    public boolean isMinorityCommunity() {
        return minorityCommunity;
    }

    public void setMinorityCommunity(boolean minorityCommunity) {
        this.minorityCommunity = minorityCommunity;
    }

    public boolean isFirstGraduate() {
        return firstGraduate;
    }

    public void setFirstGraduate(boolean firstGraduate) {
        this.firstGraduate = firstGraduate;
    }
}