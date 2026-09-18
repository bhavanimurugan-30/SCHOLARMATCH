package com.example.scholarmatch.certificatestage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class CertificateStageRequest {

    @NotBlank(message = "Stage name is required")
    private String stageName;

    @PositiveOrZero(message = "Min days cannot be negative")
    private Integer minDays;

    @PositiveOrZero(message = "Max days cannot be negative")
    private Integer maxDays;

    @NotNull(message = "Sequence order is required")
    @PositiveOrZero(message = "Sequence order cannot be negative")
    private Integer sequenceOrder;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public Integer getMinDays() {
        return minDays;
    }

    public void setMinDays(Integer minDays) {
        this.minDays = minDays;
    }

    public Integer getMaxDays() {
        return maxDays;
    }

    public void setMaxDays(Integer maxDays) {
        this.maxDays = maxDays;
    }

    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }
}