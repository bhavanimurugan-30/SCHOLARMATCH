package com.example.scholarmatch.certificatestage.service;

import com.example.scholarmatch.certificatestage.dto.CertificateStageRequest;
import com.example.scholarmatch.certificatestage.model.CertificateStage;
import com.example.scholarmatch.certificatestage.repository.CertificateStageRepository;
import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CertificateStageService {

    private final CertificateStageRepository certificateStageRepository;
    private final CertificateTypeRepository certificateTypeRepository;

    public CertificateStageService(CertificateStageRepository certificateStageRepository,
                                   CertificateTypeRepository certificateTypeRepository) {
        this.certificateStageRepository = certificateStageRepository;
        this.certificateTypeRepository = certificateTypeRepository;
    }

    public CertificateStage create(Long certificateTypeId, CertificateStageRequest request) {
        certificateTypeRepository.findById(certificateTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + certificateTypeId));

        CertificateStage stage = new CertificateStage();
        stage.setCertificateTypeId(certificateTypeId);
        mapRequestToEntity(request, stage);

        return certificateStageRepository.save(stage);
    }

    public CertificateStage getById(Long stageId) {
        return certificateStageRepository.findById(stageId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate stage not found with id: " + stageId));
    }

    public List<CertificateStage> getByCertificateTypeId(Long certificateTypeId) {
        certificateTypeRepository.findById(certificateTypeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + certificateTypeId));
        return certificateStageRepository.findByCertificateTypeId(certificateTypeId);
    }

    public CertificateStage update(Long stageId, CertificateStageRequest request) {
        CertificateStage existing = getById(stageId);
        mapRequestToEntity(request, existing);
        certificateStageRepository.update(stageId, existing);
        return getById(stageId);
    }

    public void delete(Long stageId) {
        getById(stageId);
        certificateStageRepository.deleteById(stageId);
    }

    private void mapRequestToEntity(CertificateStageRequest request, CertificateStage stage) {
        stage.setStageName(request.getStageName());
        stage.setMinDays(request.getMinDays());
        stage.setMaxDays(request.getMaxDays());
        stage.setSequenceOrder(request.getSequenceOrder());
    }
}