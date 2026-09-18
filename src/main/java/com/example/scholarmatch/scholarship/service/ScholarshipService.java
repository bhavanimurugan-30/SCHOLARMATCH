package com.example.scholarmatch.scholarship.service;

import com.example.scholarmatch.admin.repository.AdminRepository;
import com.example.scholarmatch.adminactivitylog.service.AdminActivityLogService;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.notification.service.NotificationService;
import com.example.scholarmatch.scholarship.dto.ScholarshipRequest;
import com.example.scholarmatch.scholarship.dto.ScholarshipSearchCriteria;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.scholarshipviewlog.service.ScholarshipViewLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final AdminRepository adminRepository;
    private final InstitutionRepository institutionRepository;
    private final AdminActivityLogService adminActivityLogService;
    private final ScholarshipViewLogService scholarshipViewLogService;
    private final NotificationService notificationService;

    public ScholarshipService(
            ScholarshipRepository scholarshipRepository,
            AdminRepository adminRepository,
            InstitutionRepository institutionRepository,
            AdminActivityLogService adminActivityLogService,
            ScholarshipViewLogService scholarshipViewLogService,
            NotificationService notificationService) {

        this.scholarshipRepository = scholarshipRepository;
        this.adminRepository = adminRepository;
        this.institutionRepository = institutionRepository;
        this.adminActivityLogService = adminActivityLogService;
        this.scholarshipViewLogService = scholarshipViewLogService;
        this.notificationService = notificationService;
    }

    /**
     * Admin directly adding a government/private scholarship.
     * Goes live immediately as APPROVED.
     */
    public Scholarship createByAdmin(
            Long adminId,
            ScholarshipRequest request) {

        adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: "
                                        + adminId
                        )
                );

        if ("INSTITUTION".equals(request.getSourceType())) {

            throw new IllegalArgumentException(
                    "Admin-created scholarships cannot use source type "
                            + "INSTITUTION; use the institution submission endpoint"
            );
        }

        Scholarship s =
                mapRequestToEntity(request);

        s.setSubmittedByAdminId(adminId);
        s.setApprovalStatus("APPROVED");
        s.setApprovedByAdminId(adminId);
        s.setActive(true);

        return scholarshipRepository.save(s);
    }

    /**
     * Institution self-service scholarship submission.
     *
     * Scholarship enters PENDING_APPROVAL until
     * an admin reviews it.
     *
     * Admin receives a notification when a new
     * scholarship is submitted.
     */
    public Scholarship submitByInstitution(
            Long institutionId,
            ScholarshipRequest request) {

        institutionRepository.findById(institutionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );

        Scholarship s =
                mapRequestToEntity(request);

        s.setSourceType("INSTITUTION");
        s.setProviderInstitutionId(institutionId);
        s.setSubmittedByInstitutionId(institutionId);
        s.setApprovalStatus("PENDING_APPROVAL");
        s.setActive(true);

        Scholarship saved =
                scholarshipRepository.save(s);

        /*
         * Notify ADMIN about the new scholarship.
         */
        notificationService.notifyAdmin(
                "SCHOLARSHIP_SUBMITTED",
                saved.getScholarshipId(),
                "New scholarship submitted",
                "New scholarship submitted: "
                        + saved.getTitle()
                        + "."
        );

        return saved;
    }

    public Scholarship getById(
            Long scholarshipId) {

        return scholarshipRepository.findById(
                        scholarshipId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scholarship not found with id: "
                                        + scholarshipId
                        )
                );
    }

    /**
     * Increments the view counter and returns
     * the scholarship.
     */
    public Scholarship view(
            Long scholarshipId) {

        Scholarship s =
                getById(scholarshipId);

        scholarshipRepository.incrementViewCount(
                scholarshipId
        );

        return getById(scholarshipId);
    }

    public List<Scholarship> search(
            ScholarshipSearchCriteria criteria) {

        return scholarshipRepository.search(criteria);
    }

    public List<Scholarship> getAll() {

        return scholarshipRepository.findAll();
    }

    public List<Scholarship> getActiveApproved() {

        return scholarshipRepository.findActiveApproved();
    }

    public List<Scholarship> getByPrimaryCategory(
            String primaryCategory) {

        return scholarshipRepository.findByPrimaryCategory(
                primaryCategory
        );
    }

    public List<Scholarship> getByApprovalStatus(
            String status) {

        return scholarshipRepository.findByApprovalStatus(
                status
        );
    }

    public List<Scholarship> getBySourceType(
            String sourceType) {

        return scholarshipRepository.findBySourceType(
                sourceType
        );
    }

    public List<Scholarship> getByProviderInstitutionId(
            Long institutionId) {

        institutionRepository.findById(institutionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );

        return scholarshipRepository
                .findByProviderInstitutionId(
                        institutionId
                );
    }

    public Scholarship update(
            Long scholarshipId,
            ScholarshipRequest request) {

        Scholarship existing =
                getById(scholarshipId);

        existing.setTitle(
                request.getTitle()
        );

        existing.setDescription(
                request.getDescription()
        );

        existing.setFundingBodyName(
                request.getFundingBodyName()
        );

        if (request.getSourceType() != null
                && !request.getSourceType().isBlank()) {

            existing.setSourceType(
                    request.getSourceType()
            );
        }

        existing.setAmount(
                request.getAmount()
        );

        existing.setApplicationMode(
                request.getApplicationMode() == null
                        || request.getApplicationMode().isBlank()
                        ? existing.getApplicationMode()
                        : request.getApplicationMode()
        );

        existing.setOfficialApplicationLink(
                request.getOfficialApplicationLink()
        );

        existing.setEligibleCategories(
                request.getEligibleCategories()
        );

        existing.setMinAnnualIncome(
                request.getMinAnnualIncome()
        );

        existing.setMaxAnnualIncome(
                request.getMaxAnnualIncome()
        );

        existing.setEligibleCourses(
                request.getEligibleCourses()
        );

        existing.setEducationLevel(
                request.getEducationLevel() == null
                        || request.getEducationLevel().isBlank()
                        ? existing.getEducationLevel()
                        : request.getEducationLevel()
        );

        existing.setMinMarksCgpa(
                request.getMinMarksCgpa()
        );

        existing.setEligibleStates(
                request.getEligibleStates()
        );

        existing.setEligibleSpecialStatus(
                request.getEligibleSpecialStatus()
        );

        existing.setDeadline(
                request.getDeadline()
        );

        if (request.getPrimaryCategory() != null
                && !request.getPrimaryCategory().isBlank()) {

            existing.setPrimaryCategory(
                    request.getPrimaryCategory()
            );
        }

        existing.setRequiredDocuments(
                request.getRequiredDocuments()
        );

        scholarshipRepository.update(
                scholarshipId,
                existing
        );

        return getById(scholarshipId);
    }

    /**
     * Admin approves an institution-submitted scholarship.
     */
    public Scholarship approve(
            Long scholarshipId,
            Long adminId) {

        Scholarship s =
                getById(scholarshipId);

        adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: "
                                        + adminId
                        )
                );

        scholarshipRepository.updateApprovalStatus(
                scholarshipId,
                "APPROVED",
                adminId,
                null
        );

        adminActivityLogService.record(
                adminId,
                "APPROVE_SCHOLARSHIP",
                "scholarship",
                scholarshipId,
                "Approved: " + s.getTitle()
        );

        return getById(scholarshipId);
    }

    /**
     * Admin rejects an institution-submitted scholarship.
     */
    public Scholarship reject(
            Long scholarshipId,
            Long adminId,
            String reason) {

        Scholarship s =
                getById(scholarshipId);

        adminRepository.findById(adminId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admin not found with id: "
                                        + adminId
                        )
                );

        if (reason == null || reason.isBlank()) {

            throw new IllegalArgumentException(
                    "A rejection reason is required"
            );
        }

        scholarshipRepository.updateApprovalStatus(
                scholarshipId,
                "REJECTED",
                adminId,
                reason
        );

        adminActivityLogService.record(
                adminId,
                "REJECT_SCHOLARSHIP",
                "scholarship",
                scholarshipId,
                "Rejected: "
                        + s.getTitle()
                        + " — "
                        + reason
        );

        return getById(scholarshipId);
    }

    public void deactivate(
            Long scholarshipId) {

        getById(scholarshipId);

        scholarshipRepository.updateActiveStatus(
                scholarshipId,
                false
        );
    }

    public void activate(
            Long scholarshipId) {

        getById(scholarshipId);

        scholarshipRepository.updateActiveStatus(
                scholarshipId,
                true
        );
    }

    public void delete(
            Long scholarshipId) {

        getById(scholarshipId);

        scholarshipRepository.deleteById(
                scholarshipId
        );
    }

    private Scholarship mapRequestToEntity(
            ScholarshipRequest request) {

        Scholarship s =
                new Scholarship();

        s.setTitle(
                request.getTitle()
        );

        s.setDescription(
                request.getDescription()
        );

        s.setSourceType(
                request.getSourceType()
        );

        /*
         * Primary category must come from the request.
         * Do not silently default to STATE.
         */
        if (request.getPrimaryCategory() == null
                || request.getPrimaryCategory().isBlank()) {

            throw new IllegalArgumentException(
                    "Primary category is required"
            );
        }

        s.setPrimaryCategory(
                request.getPrimaryCategory()
        );

        s.setFundingBodyName(
                request.getFundingBodyName()
        );

        s.setAmount(
                request.getAmount()
        );

        s.setApplicationMode(
                request.getApplicationMode() == null
                        || request.getApplicationMode().isBlank()
                        ? "EXTERNAL_REDIRECT"
                        : request.getApplicationMode()
        );

        s.setOfficialApplicationLink(
                request.getOfficialApplicationLink()
        );

        s.setEligibleCategories(
                request.getEligibleCategories()
        );

        s.setMinAnnualIncome(
                request.getMinAnnualIncome()
        );

        s.setMaxAnnualIncome(
                request.getMaxAnnualIncome()
        );

        s.setEligibleCourses(
                request.getEligibleCourses()
        );

        s.setEducationLevel(
                request.getEducationLevel() == null
                        || request.getEducationLevel().isBlank()
                        ? "ANY"
                        : request.getEducationLevel()
        );

        s.setMinMarksCgpa(
                request.getMinMarksCgpa()
        );

        s.setEligibleStates(
                request.getEligibleStates()
        );

        s.setEligibleSpecialStatus(
                request.getEligibleSpecialStatus()
        );

        s.setRequiredDocuments(
                request.getRequiredDocuments()
        );

        s.setDeadline(
                request.getDeadline()
        );

        return s;
    }
}