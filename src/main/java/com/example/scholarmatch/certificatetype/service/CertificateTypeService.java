package com.example.scholarmatch.certificatetype.service;

import com.example.scholarmatch.certificatetype.dto.CertificateTypeRequest;
import com.example.scholarmatch.certificatetype.model.CertificateType;
import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateTypeService {

    private final CertificateTypeRepository certificateTypeRepository;

    public CertificateTypeService(CertificateTypeRepository certificateTypeRepository) {
        this.certificateTypeRepository = certificateTypeRepository;
    }

    public CertificateType create(CertificateTypeRequest request) {
        if (certificateTypeRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Certificate type already exists: " + request.getName());
        }

        CertificateType type = new CertificateType();
        mapRequestToEntity(request, type);

        return certificateTypeRepository.save(type);
    }

    public CertificateType getById(Long certificateTypeId) {
        return certificateTypeRepository.findById(certificateTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + certificateTypeId));
    }

    public List<CertificateType> getAll() {
        return certificateTypeRepository.findAll();
    }

    public CertificateType update(Long certificateTypeId, CertificateTypeRequest request) {
        CertificateType existing = getById(certificateTypeId);
        mapRequestToEntity(request, existing);
        certificateTypeRepository.update(certificateTypeId, existing);
        return getById(certificateTypeId);
    }

    public void delete(Long certificateTypeId) {
        getById(certificateTypeId);
        certificateTypeRepository.deleteById(certificateTypeId);
    }

    private void mapRequestToEntity(CertificateTypeRequest request, CertificateType type) {
        type.setName(request.getName());
        type.setIssuingAuthority(request.getIssuingAuthority());
        type.setMinProcessingDays(request.getMinProcessingDays());
        type.setMaxProcessingDays(request.getMaxProcessingDays());
        type.setTatkaalAvailable(request.isTatkaalAvailable());
        type.setOfficialApplyLink(request.getOfficialApplyLink());
        type.setRequiredDocuments(request.getRequiredDocuments());
    }
}