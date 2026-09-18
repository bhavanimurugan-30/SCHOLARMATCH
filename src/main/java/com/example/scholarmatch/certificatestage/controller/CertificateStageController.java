package com.example.scholarmatch.certificatestage.controller;

import com.example.scholarmatch.certificatestage.dto.CertificateStageRequest;
import com.example.scholarmatch.certificatestage.model.CertificateStage;
import com.example.scholarmatch.certificatestage.service.CertificateStageService;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificate-types/{certificateTypeId}/stages")
public class CertificateStageController {

    private final CertificateStageService certificateStageService;

    public CertificateStageController(CertificateStageService certificateStageService) {
        this.certificateStageService = certificateStageService;
    }

    @PostMapping
    public ApiResponse<CertificateStage> create(@PathVariable Long certificateTypeId,
                                                @Valid @RequestBody CertificateStageRequest request) {
        return ApiResponse.success("Stage created", certificateStageService.create(certificateTypeId, request));
    }

    @GetMapping
    public ApiResponse<List<CertificateStage>> getAll(@PathVariable Long certificateTypeId) {
        return ApiResponse.success(certificateStageService.getByCertificateTypeId(certificateTypeId));
    }

    @PutMapping("/{stageId}")
    public ApiResponse<CertificateStage> update(@PathVariable Long certificateTypeId,
                                                @PathVariable Long stageId,
                                                @Valid @RequestBody CertificateStageRequest request) {
        return ApiResponse.success("Stage updated", certificateStageService.update(stageId, request));
    }

    @DeleteMapping("/{stageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long certificateTypeId, @PathVariable Long stageId) {
        certificateStageService.delete(stageId);
    }
}