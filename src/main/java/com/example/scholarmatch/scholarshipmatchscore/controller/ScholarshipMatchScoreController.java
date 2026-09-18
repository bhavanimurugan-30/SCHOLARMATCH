package com.example.scholarmatch.scholarshipmatchscore.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.scholarshipmatchscore.dto.EligibilityDetailsResponse;
import com.example.scholarmatch.scholarshipmatchscore.model.ScholarshipMatchScore;
import com.example.scholarmatch.scholarshipmatchscore.service.EligibilityEngineService;
import com.example.scholarmatch.security.CustomUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}")
public class ScholarshipMatchScoreController {

    private final EligibilityEngineService eligibilityEngineService;

    public ScholarshipMatchScoreController(EligibilityEngineService eligibilityEngineService) {
        this.eligibilityEngineService = eligibilityEngineService;
    }

    private void assertOwnership(Long studentId, CustomUserPrincipal principal) {
        if (principal != null && "STUDENT".equals(principal.getRole()) && !studentId.equals(principal.getId())) {
            throw new AccessDeniedException("You can only access your own match scores");
        }
    }

    @GetMapping("/match-scores/recompute")
    public ApiResponse<List<ScholarshipMatchScore>> recomputeAll(@PathVariable Long studentId,
                                                                 @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success("Match scores recomputed",
                eligibilityEngineService.computeAllForStudent(studentId));
    }

    @GetMapping("/match-scores")
    public ApiResponse<List<ScholarshipMatchScore>> getAll(@PathVariable Long studentId,
                                                           @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success(eligibilityEngineService.getForStudent(studentId));
    }

    @GetMapping("/scholarships/{scholarshipId}/match-score/recompute")
    public ApiResponse<ScholarshipMatchScore> recomputeOne(@PathVariable Long studentId,
                                                           @PathVariable Long scholarshipId,
                                                           @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success("Match score computed",
                eligibilityEngineService.computeAndSave(studentId, scholarshipId));
    }

    @GetMapping("/scholarships/{scholarshipId}/eligibility-details")
    public ApiResponse<EligibilityDetailsResponse> eligibilityDetails(
            @PathVariable Long studentId,
            @PathVariable Long scholarshipId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success(eligibilityEngineService.getEligibilityDetails(studentId, scholarshipId));
    }

    @GetMapping("/eligible-scholarships")
    public ApiResponse<List<EligibilityDetailsResponse>> eligibleScholarships(
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success(eligibilityEngineService.getEligibleScholarships(studentId));
    }

    @GetMapping("/scholarships/{scholarshipId}/match-score")
    public ApiResponse<ScholarshipMatchScore> getOne(@PathVariable Long studentId,
                                                     @PathVariable Long scholarshipId,
                                                     @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success(eligibilityEngineService.getExisting(studentId, scholarshipId));
    }
}