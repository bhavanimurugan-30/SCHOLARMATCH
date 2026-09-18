package com.example.scholarmatch.institution.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.institution.dto.InstitutionRequest;
import com.example.scholarmatch.institution.model.Institution;
import com.example.scholarmatch.institution.service.InstitutionService;
import com.example.scholarmatch.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    public InstitutionController(InstitutionService institutionService) {
        this.institutionService = institutionService;
    }

    @PostMapping
    public ApiResponse<Institution> register(@Valid @RequestBody InstitutionRequest request) {
        return ApiResponse.success("Institution registered. Awaiting admin verification.",
                institutionService.register(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<Institution> getById(@PathVariable("id") Long id) {
        requireOwnerOrAdmin(id);
        return ApiResponse.success(institutionService.getById(id));
    }

    // Listing/filtering every institution is an admin-only capability
    // (used by the admin console to review pending institutions).
    @GetMapping
    public ApiResponse<List<Institution>> getAll(
            @RequestParam(value = "status", required = false) String status) {
        requireAdmin();
        if (status != null && !status.isBlank()) {
            return ApiResponse.success(institutionService.getByVerificationStatus(status));
        }
        return ApiResponse.success(institutionService.getAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<Institution> update(@PathVariable("id") Long id,
                                           @Valid @RequestBody InstitutionRequest request) {
        requireOwnerOrAdmin(id);
        return ApiResponse.success("Institution updated", institutionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        requireAdmin();
        institutionService.delete(id);
    }

    /**
     * Allows an ADMIN, or the INSTITUTION whose own id matches the path
     * variable, to proceed. Anyone else is rejected with 403.
     */
    private void requireOwnerOrAdmin(Long institutionId) {
        CustomUserPrincipal principal = currentPrincipal();
        if (principal == null) {
            throw new AccessDeniedException("Authentication required");
        }
        boolean isAdmin = "ADMIN".equals(principal.getRole());
        boolean isOwner = "INSTITUTION".equals(principal.getRole()) && principal.getId().equals(institutionId);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to access this institution's data");
        }
    }

    private void requireAdmin() {
        CustomUserPrincipal principal = currentPrincipal();
        if (principal == null || !"ADMIN".equals(principal.getRole())) {
            throw new AccessDeniedException("Only an admin can perform this action");
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