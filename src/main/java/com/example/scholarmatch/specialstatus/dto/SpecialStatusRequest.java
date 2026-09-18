package com.example.scholarmatch.specialstatus.dto;

public class SpecialStatusRequest {

    private boolean singleGirlChild;
    private boolean orphanSingleParent;
    private boolean exServicemenDependent;
    private boolean sportsQuota;
    private boolean minorityCommunity;
    private boolean firstGraduate;

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