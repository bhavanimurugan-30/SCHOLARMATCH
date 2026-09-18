package com.example.scholarmatch.certificateportallink.service;

import com.example.scholarmatch.certificateportallink.dto.CertificatePortalLinkRequest;
import com.example.scholarmatch.certificateportallink.model.CertificatePortalLink;
import com.example.scholarmatch.certificateportallink.repository.CertificatePortalLinkRepository;
import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificatePortalLinkService {

    private final CertificatePortalLinkRepository certificatePortalLinkRepository;
    private final CertificateTypeRepository certificateTypeRepository;

    public CertificatePortalLinkService(CertificatePortalLinkRepository certificatePortalLinkRepository,
                                        CertificateTypeRepository certificateTypeRepository) {
        this.certificatePortalLinkRepository = certificatePortalLinkRepository;
        this.certificateTypeRepository = certificateTypeRepository;
    }

    public CertificatePortalLink create(Long certificateTypeId, CertificatePortalLinkRequest request) {
        certificateTypeRepository.findById(certificateTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + certificateTypeId));

        CertificatePortalLink link = new CertificatePortalLink();
        link.setCertificateTypeId(certificateTypeId);
        mapRequestToEntity(request, link);

        return certificatePortalLinkRepository.save(link);
    }

    public CertificatePortalLink getById(Long portalLinkId) {
        return certificatePortalLinkRepository.findById(portalLinkId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate portal link not found with id: " + portalLinkId));
    }

    public List<CertificatePortalLink> getByCertificateTypeId(Long certificateTypeId) {
        certificateTypeRepository.findById(certificateTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + certificateTypeId));
        return certificatePortalLinkRepository.findByCertificateTypeId(certificateTypeId);
    }

    public CertificatePortalLink update(Long portalLinkId, CertificatePortalLinkRequest request) {
        CertificatePortalLink existing = getById(portalLinkId);
        mapRequestToEntity(request, existing);
        certificatePortalLinkRepository.update(portalLinkId, existing);
        return getById(portalLinkId);
    }

    public void delete(Long portalLinkId) {
        getById(portalLinkId);
        certificatePortalLinkRepository.deleteById(portalLinkId);
    }

    private void mapRequestToEntity(CertificatePortalLinkRequest request, CertificatePortalLink link) {
        link.setState(request.getState());
        link.setPortalName(request.getPortalName());
        link.setPortalUrl(request.getPortalUrl());
        link.setLastVerifiedDate(request.getLastVerifiedDate());
    }
}