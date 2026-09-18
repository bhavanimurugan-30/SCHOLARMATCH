package com.example.scholarmatch.institutionverificationlog.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.institutionverificationlog.dto.InstitutionDecisionRequest;
import com.example.scholarmatch.institutionverificationlog.dto.InstitutionVerificationLogRequest;
import com.example.scholarmatch.institutionverificationlog.model.InstitutionVerificationLog;
import com.example.scholarmatch.institutionverificationlog.service.InstitutionVerificationLogService;
import com.example.scholarmatch.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions/{institutionId}/verification-log")
public class InstitutionVerificationLogController {

    private final InstitutionVerificationLogService verificationLogService;

    public InstitutionVerificationLogController(InstitutionVerificationLogService verificationLogService) {
        this.verificationLogService = verificationLogService;
    }

    // Admin-only: the SecurityConfig rule for /api/institutions/** allows both
    // ADMIN and INSTITUTION roles, so without this check an authenticated
    // institution could submit its own SUBMITTED log entries out of band.
    @PostMapping("/submit")
    public ApiResponse<InstitutionVerificationLog> submit(@PathVariable Long institutionId) {
        requireAdmin();
        return ApiResponse.success("Submission recorded", verificationLogService.recordSubmission(institutionId));
    }

    // Admin-only: approve/reject/other decisions must never be reachable by an
    // INSTITUTION-role token, even though /api/institutions/** otherwise
    // allows both ADMIN and INSTITUTION.
    @PostMapping("/decision")
    public ApiResponse<InstitutionVerificationLog> decide(
            @PathVariable Long institutionId,
            @Valid @RequestBody InstitutionVerificationLogRequest request) {
        requireAdmin();
        return ApiResponse.success("Decision recorded", verificationLogService.recordDecision(institutionId, request));
    }

    @PostMapping("/approve")
    public ApiResponse<InstitutionVerificationLog> approve(
            @PathVariable Long institutionId,
            @Valid @RequestBody InstitutionDecisionRequest request) {
        requireAdmin();
        InstitutionVerificationLogRequest decision = new InstitutionVerificationLogRequest();
        decision.setAdminId(request.getAdminId());
        decision.setAction("APPROVED");
        decision.setReason(request.getReason());
        return ApiResponse.success("Institution approved", verificationLogService.recordDecision(institutionId, decision));
    }

    @PostMapping("/reject")
    public ApiResponse<InstitutionVerificationLog> reject(
            @PathVariable Long institutionId,
            @Valid @RequestBody InstitutionDecisionRequest request) {
        requireAdmin();
        InstitutionVerificationLogRequest decision = new InstitutionVerificationLogRequest();
        decision.setAdminId(request.getAdminId());
        decision.setAction("REJECTED");
        decision.setReason(request.getReason());
        return ApiResponse.success("Institution rejected", verificationLogService.recordDecision(institutionId, decision));
    }

    // Admin can view any institution's log; the institution itself can view
    // only its own (e.g. to read its rejection reason).
    @GetMapping
    public ApiResponse<List<InstitutionVerificationLog>> getAll(@PathVariable Long institutionId) {
        requireOwnerOrAdmin(institutionId);
        return ApiResponse.success(verificationLogService.getByInstitutionId(institutionId));
    }

    private void requireAdmin() {
        CustomUserPrincipal principal = currentPrincipal();
        if (principal == null || !"ADMIN".equals(principal.getRole())) {
            throw new AccessDeniedException("Only an admin can perform this action");
        }
    }

    private void requireOwnerOrAdmin(Long institutionId) {
        CustomUserPrincipal principal = currentPrincipal();
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        boolean isAdmin = "ADMIN".equals(principal.getRole());
        boolean isOwner = "INSTITUTION".equals(principal.getRole()) && principal.getId().equals(institutionId);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to access this institution's verification log");
        }
    }

    private CustomUserPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            return principal;
        }
        return null;
    }
}