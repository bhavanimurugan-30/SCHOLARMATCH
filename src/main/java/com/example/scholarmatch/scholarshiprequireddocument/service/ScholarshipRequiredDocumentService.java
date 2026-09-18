package com.example.scholarmatch.scholarshiprequireddocument.service;

import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.scholarshiprequireddocument.dto.ScholarshipRequiredDocumentRequest;
import com.example.scholarmatch.scholarshiprequireddocument.model.ScholarshipRequiredDocument;
import com.example.scholarmatch.scholarshiprequireddocument.repository.ScholarshipRequiredDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipRequiredDocumentService {

    private final ScholarshipRequiredDocumentRepository requiredDocumentRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final CertificateTypeRepository certificateTypeRepository;

    public ScholarshipRequiredDocumentService(ScholarshipRequiredDocumentRepository requiredDocumentRepository,
                                              ScholarshipRepository scholarshipRepository,
                                              CertificateTypeRepository certificateTypeRepository) {
        this.requiredDocumentRepository = requiredDocumentRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.certificateTypeRepository = certificateTypeRepository;
    }

    public ScholarshipRequiredDocument add(Long scholarshipId, ScholarshipRequiredDocumentRequest request) {
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));
        certificateTypeRepository.findById(request.getCertificateTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + request.getCertificateTypeId()));

        if (requiredDocumentRepository.exists(scholarshipId, request.getCertificateTypeId())) {
            throw new IllegalArgumentException("This document is already required for this scholarship");
        }

        ScholarshipRequiredDocument doc = new ScholarshipRequiredDocument();
        doc.setScholarshipId(scholarshipId);
        doc.setCertificateTypeId(request.getCertificateTypeId());
        doc.setMandatory(request.isMandatory());

        return requiredDocumentRepository.save(doc);
    }

    public List<ScholarshipRequiredDocument> getByScholarshipId(Long scholarshipId) {
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));
        return requiredDocumentRepository.findByScholarshipId(scholarshipId);
    }

    public void remove(Long scholarshipId, Long certificateTypeId) {
        if (!requiredDocumentRepository.exists(scholarshipId, certificateTypeId)) {
            throw new ResourceNotFoundException("This document requirement does not exist for this scholarship");
        }
        requiredDocumentRepository.deleteByScholarshipIdAndCertificateTypeId(scholarshipId, certificateTypeId);
    }
}