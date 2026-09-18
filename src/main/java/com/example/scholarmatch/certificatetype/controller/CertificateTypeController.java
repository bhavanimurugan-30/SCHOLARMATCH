package com.example.scholarmatch.certificatetype.controller;

import com.example.scholarmatch.certificatetype.dto.CertificateTypeRequest;
import com.example.scholarmatch.certificatetype.model.CertificateType;
import com.example.scholarmatch.certificatetype.service.CertificateTypeService;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificate-types")
public class CertificateTypeController {

    private final CertificateTypeService certificateTypeService;

    public CertificateTypeController(CertificateTypeService certificateTypeService) {
        this.certificateTypeService = certificateTypeService;
    }

    @PostMapping
    public ApiResponse<CertificateType> create(@Valid @RequestBody CertificateTypeRequest request) {
        return ApiResponse.success("Certificate type created", certificateTypeService.create(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CertificateType> getById(@PathVariable("id") Long id) {
        return ApiResponse.success(certificateTypeService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<CertificateType>> getAll() {
        return ApiResponse.success(certificateTypeService.getAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<CertificateType> update(@PathVariable("id") Long id,
                                               @Valid @RequestBody CertificateTypeRequest request) {
        return ApiResponse.success("Certificate type updated", certificateTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        certificateTypeService.delete(id);
    }
}