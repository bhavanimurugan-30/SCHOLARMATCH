package com.example.scholarmatch.scholarship.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.scholarship.dto.ScholarshipRequest;
import com.example.scholarmatch.scholarship.dto.ScholarshipSearchCriteria;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.service.ScholarshipService;
import com.example.scholarmatch.scholarshipviewlog.model.ScholarshipViewLog;
import com.example.scholarmatch.scholarshipviewlog.service.ScholarshipViewLogService;
import com.example.scholarmatch.searchlog.dto.SearchLogRequest;
import com.example.scholarmatch.searchlog.service.SearchLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scholarships")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;
    private final SearchLogService searchLogService;
    private final ScholarshipViewLogService viewLogService;

    public ScholarshipController(ScholarshipService scholarshipService,
                                 SearchLogService searchLogService,
                                 ScholarshipViewLogService viewLogService) {
        this.scholarshipService = scholarshipService;
        this.searchLogService = searchLogService;
        this.viewLogService = viewLogService;
    }

    @GetMapping("/search")
    public ApiResponse<List<Scholarship>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Double income,
            @RequestParam(required = false) String sourceType,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(value = "deadlineBefore", required = false)
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate deadlineBefore,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Double minMatchPercentage,
            @RequestParam(defaultValue = "deadline") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        ScholarshipSearchCriteria criteria = new ScholarshipSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setCategory(category);
        criteria.setState(state);
        criteria.setCourse(course);
        criteria.setIncome(income);
        criteria.setSourceType(sourceType);
        criteria.setMinAmount(minAmount);
        criteria.setMaxAmount(maxAmount);
        criteria.setDeadlineBefore(deadlineBefore);
        criteria.setStudentId(studentId);
        criteria.setMinMatchPercentage(minMatchPercentage);
        criteria.setSortBy(sortBy);
        criteria.setSortDir(sortDir);

        List<Scholarship> results = scholarshipService.search(criteria);

        if (studentId != null && keyword != null && !keyword.isBlank()) {
            SearchLogRequest logRequest = new SearchLogRequest();
            logRequest.setStudentId(studentId);
            logRequest.setSearchQuery(keyword);
            logRequest.setFiltersApplied(java.util.Map.of(
                    "category", category == null ? "" : category,
                    "state", state == null ? "" : state,
                    "course", course == null ? "" : course,
                    "sourceType", sourceType == null ? "" : sourceType,
                    "sortBy", sortBy));
            try { searchLogService.record(logRequest); } catch (Exception ignored) { }
        }

        return ApiResponse.success(results);
    }

    @GetMapping("/views/recent")
    public ApiResponse<List<ScholarshipViewLog>> recentViews(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ApiResponse.success(viewLogService.getRecent(limit));
    }

    @GetMapping("/views/student/{studentId}")
    public ApiResponse<List<ScholarshipViewLog>> studentRecentlyViewed(@PathVariable Long studentId) {
        return ApiResponse.success(viewLogService.getByStudentId(studentId));
    }

    @GetMapping("/most-viewed")
    public ApiResponse<List<Map<String, Object>>> mostViewed(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ApiResponse.success(viewLogService.getMostViewed(limit));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/{adminId}")
    public ApiResponse<Scholarship> createByAdmin(@PathVariable Long adminId,
                                                  @Valid @RequestBody ScholarshipRequest request) {
        return ApiResponse.success("Scholarship created", scholarshipService.createByAdmin(adminId, request));
    }

    @PreAuthorize("hasAnyRole('INSTITUTION', 'ADMIN')")
    @PostMapping("/institution/{institutionId}")
    public ApiResponse<Scholarship> submitByInstitution(@PathVariable Long institutionId,
                                                        @Valid @RequestBody ScholarshipRequest request) {
        return ApiResponse.success("Scholarship submitted for approval",
                scholarshipService.submitByInstitution(institutionId, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<Scholarship> getById(@PathVariable("id") Long id,
                                            @RequestParam(value = "trackView", defaultValue = "true") boolean trackView) {
        Scholarship s = trackView ? scholarshipService.view(id) : scholarshipService.getById(id);
        return ApiResponse.success(s);
    }

    @GetMapping
    public ApiResponse<List<Scholarship>> getAll(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sourceType", required = false) String sourceType,
            @RequestParam(value = "primaryCategory", required = false) String primaryCategory,
            @RequestParam(value = "activeOnly", defaultValue = "false") boolean activeOnly) {

        if (primaryCategory != null && !primaryCategory.isBlank()) {
            return ApiResponse.success(scholarshipService.getByPrimaryCategory(primaryCategory.toUpperCase()));
        }
        if (activeOnly) {
            return ApiResponse.success(scholarshipService.getActiveApproved());
        }
        if (status != null && !status.isBlank()) {
            return ApiResponse.success(scholarshipService.getByApprovalStatus(status));
        }
        if (sourceType != null && !sourceType.isBlank()) {
            return ApiResponse.success(scholarshipService.getBySourceType(sourceType));
        }
        return ApiResponse.success(scholarshipService.getAll());
    }

    @GetMapping("/institution/{institutionId}")
    public ApiResponse<List<Scholarship>> getByInstitution(@PathVariable Long institutionId) {
        return ApiResponse.success(scholarshipService.getByProviderInstitutionId(institutionId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<Scholarship> update(@PathVariable("id") Long id,
                                           @Valid @RequestBody ScholarshipRequest request) {
        return ApiResponse.success("Scholarship updated", scholarshipService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/approve")
    public ApiResponse<Scholarship> approve(@PathVariable("id") Long id,
                                            @RequestParam("adminId") Long adminId) {
        return ApiResponse.success("Scholarship approved", scholarshipService.approve(id, adminId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/reject")
    public ApiResponse<Scholarship> reject(@PathVariable("id") Long id,
                                           @RequestParam("adminId") Long adminId,
                                           @RequestParam("reason") String reason) {
        return ApiResponse.success("Scholarship rejected", scholarshipService.reject(id, adminId, reason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivate(@PathVariable("id") Long id) {
        scholarshipService.deactivate(id);
        return ApiResponse.success("Scholarship deactivated", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activate(@PathVariable("id") Long id) {
        scholarshipService.activate(id);
        return ApiResponse.success("Scholarship activated", null);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        scholarshipService.delete(id);
    }
}