package com.example.scholarmatch.institutionverificationlog.service;

import com.example.scholarmatch.admin.repository.AdminRepository;
import com.example.scholarmatch.adminactivitylog.service.AdminActivityLogService;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.institutionverificationlog.dto.InstitutionVerificationLogRequest;
import com.example.scholarmatch.institutionverificationlog.model.InstitutionVerificationLog;
import com.example.scholarmatch.institutionverificationlog.repository.InstitutionVerificationLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionVerificationLogService {

    private final InstitutionVerificationLogRepository verificationLogRepository;
    private final InstitutionRepository institutionRepository;
    private final AdminRepository adminRepository;
    private final AdminActivityLogService adminActivityLogService;

    public InstitutionVerificationLogService(InstitutionVerificationLogRepository verificationLogRepository,
                                             InstitutionRepository institutionRepository,
                                             AdminRepository adminRepository,
                                             AdminActivityLogService adminActivityLogService) {
        this.verificationLogRepository = verificationLogRepository;
        this.institutionRepository = institutionRepository;
        this.adminRepository = adminRepository;
        this.adminActivityLogService = adminActivityLogService;
    }

    /**
     * Records a SUBMITTED entry. Called right after institution self-registration
     * (Section 8.1: "Institution registers ... Account status is set to PENDING").
     */
    public InstitutionVerificationLog recordSubmission(Long institutionId) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));

        InstitutionVerificationLog log = new InstitutionVerificationLog();
        log.setInstitutionId(institutionId);
        log.setAdminId(null);
        log.setAction("SUBMITTED");
        log.setReason(null);

        return verificationLogRepository.save(log);
    }

    /**
     * Admin decision on an institution (Section 8.1/8.2): APPROVED, REJECTED,
     * INFO_REQUESTED, or SUSPENDED. Reason is mandatory in practice when action = REJECTED.
     */
    public InstitutionVerificationLog recordDecision(Long institutionId, InstitutionVerificationLogRequest request) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));

        adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + request.getAdminId()));

        if ("REJECTED".equals(request.getAction())
                && (request.getReason() == null || request.getReason().isBlank())) {
            throw new IllegalArgumentException("A reason is required when rejecting an institution");
        }

        InstitutionVerificationLog log = new InstitutionVerificationLog();
        log.setInstitutionId(institutionId);
        log.setAdminId(request.getAdminId());
        log.setAction(request.getAction());
        log.setReason(request.getReason());

        InstitutionVerificationLog saved = verificationLogRepository.save(log);

        adminActivityLogService.record(request.getAdminId(), "INSTITUTION_" + request.getAction(),
                "institution", institutionId, request.getReason());

        // Keep institution.verification_status in sync with the latest decision.
        String newStatus = switch (request.getAction()) {
            case "APPROVED" -> "APPROVED";
            case "REJECTED" -> "REJECTED";
            case "SUSPENDED" -> "SUSPENDED";
            default -> null; // INFO_REQUESTED does not change the status
        };
        if (newStatus != null) {
            institutionRepository.updateVerificationStatus(institutionId, newStatus);
        }

        return saved;
    }

    public List<InstitutionVerificationLog> getByInstitutionId(Long institutionId) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));
        return verificationLogRepository.findByInstitutionId(institutionId);
    }
}