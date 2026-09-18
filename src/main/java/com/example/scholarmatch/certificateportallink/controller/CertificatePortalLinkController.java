package com.example.scholarmatch.certificateportallink.controller;

import com.example.scholarmatch.certificateportallink.dto.CertificatePortalLinkRequest;
import com.example.scholarmatch.certificateportallink.model.CertificatePortalLink;
import com.example.scholarmatch.certificateportallink.service.CertificatePortalLinkService;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificate-types/{certificateTypeId}/portal-links")
public class CertificatePortalLinkController {

    private final CertificatePortalLinkService certificatePortalLinkService;

    public CertificatePortalLinkController(CertificatePortalLinkService certificatePortalLinkService) {
        this.certificatePortalLinkService = certificatePortalLinkService;
    }

    @PostMapping
    public ApiResponse<CertificatePortalLink> create(@PathVariable Long certificateTypeId,
                                                     @Valid @RequestBody CertificatePortalLinkRequest request) {
        return ApiResponse.success("Portal link created",
                certificatePortalLinkService.create(certificateTypeId, request));
    }

    @GetMapping
    public ApiResponse<List<CertificatePortalLink>> getAll(@PathVariable Long certificateTypeId) {
        return ApiResponse.success(certificatePortalLinkService.getByCertificateTypeId(certificateTypeId));
    }

    @PutMapping("/{portalLinkId}")
    public ApiResponse<CertificatePortalLink> update(@PathVariable Long certificateTypeId,
                                                     @PathVariable Long portalLinkId,
                                                     @Valid @RequestBody CertificatePortalLinkRequest request) {
        return ApiResponse.success("Portal link updated",
                certificatePortalLinkService.update(portalLinkId, request));
    }

    @DeleteMapping("/{portalLinkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long certificateTypeId, @PathVariable Long portalLinkId) {
        certificatePortalLinkService.delete(portalLinkId);
    }
}