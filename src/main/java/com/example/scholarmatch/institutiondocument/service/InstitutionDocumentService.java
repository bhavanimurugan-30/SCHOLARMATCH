package com.example.scholarmatch.institutiondocument.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.institutiondocument.dto.InstitutionDocumentRequest;
import com.example.scholarmatch.institutiondocument.model.InstitutionDocument;
import com.example.scholarmatch.institutiondocument.repository.InstitutionDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionDocumentService {

    private final InstitutionDocumentRepository institutionDocumentRepository;
    private final InstitutionRepository institutionRepository;

    public InstitutionDocumentService(InstitutionDocumentRepository institutionDocumentRepository,
                                      InstitutionRepository institutionRepository) {
        this.institutionDocumentRepository = institutionDocumentRepository;
        this.institutionRepository = institutionRepository;
    }

    public InstitutionDocument upload(Long institutionId, InstitutionDocumentRequest request) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));

        InstitutionDocument doc = new InstitutionDocument();
        doc.setInstitutionId(institutionId);
        doc.setDocumentType(request.getDocumentType());
        doc.setFilePath(request.getFilePath());

        return institutionDocumentRepository.save(doc);
    }

    public InstitutionDocument getById(Long institutionDocumentId) {
        return institutionDocumentRepository.findById(institutionDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Institution document not found with id: " + institutionDocumentId));
    }

    public List<InstitutionDocument> getByInstitutionId(Long institutionId) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));
        return institutionDocumentRepository.findByInstitutionId(institutionId);
    }

    /**
     * Reserved for the AI/OCR pre-check step (Sections 5.3 & 8.3), which is flagged
     * as not-yet-implemented. Once built, that service would call this to record
     * its confidence score for the admin to review.
     */
    public InstitutionDocument setAiPrecheckConfidence(Long institutionDocumentId, Double confidence) {
        getById(institutionDocumentId);
        institutionDocumentRepository.updateAiPrecheckConfidence(institutionDocumentId, confidence);
        return getById(institutionDocumentId);
    }

    public void delete(Long institutionDocumentId) {
        getById(institutionDocumentId);
        institutionDocumentRepository.deleteById(institutionDocumentId);
    }
}