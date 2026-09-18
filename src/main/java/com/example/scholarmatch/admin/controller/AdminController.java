package com.example.scholarmatch.admin.controller;

import com.example.scholarmatch.admin.dto.AdminRequest;
import com.example.scholarmatch.admin.model.Admin;
import com.example.scholarmatch.admin.service.AdminService;
import com.example.scholarmatch.bookmark.repository.BookmarkRepository;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;
    private final ScholarshipRepository scholarshipRepository;
    private final BookmarkRepository bookmarkRepository;

    public AdminController(AdminService adminService,
                           ScholarshipRepository scholarshipRepository,
                           BookmarkRepository bookmarkRepository) {
        this.adminService = adminService;
        this.scholarshipRepository = scholarshipRepository;
        this.bookmarkRepository = bookmarkRepository;
    }

    @PostMapping
    public ApiResponse<Admin> create(@Valid @RequestBody AdminRequest request) {
        return ApiResponse.success("Admin created", adminService.create(request));
    }

    @GetMapping("/analytics/scholarships")
    public ApiResponse<List<Map<String, Object>>> scholarshipAnalytics() {
        Map<Long, Integer> appliedByScholarship = new HashMap<>();
        for (Map<String, Object> row : bookmarkRepository.findApplicationCountsByScholarship()) {
            Number scholarshipId = (Number) row.get("scholarship_id");
            Number count = (Number) row.get("application_count");
            if (scholarshipId != null) {
                appliedByScholarship.put(scholarshipId.longValue(), count != null ? count.intValue() : 0);
            }
        }

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Scholarship scholarship : scholarshipRepository.findAll()) {
            Map<String, Object> row = new HashMap<>();
            row.put("scholarshipId", scholarship.getScholarshipId());
            row.put("title", scholarship.getTitle());
            row.put("views", scholarship.getViewCount());
            row.put("applied", appliedByScholarship.getOrDefault(scholarship.getScholarshipId(), 0));
            result.add(row);
        }

        result.sort((a, b) -> {
            int viewsCompare = Integer.compare(
                    ((Number) b.get("views")).intValue(),
                    ((Number) a.get("views")).intValue());
            if (viewsCompare != 0) return viewsCompare;
            return Integer.compare(
                    ((Number) b.get("applied")).intValue(),
                    ((Number) a.get("applied")).intValue());
        });

        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Admin> getById(@PathVariable("id") Long id) {
        return ApiResponse.success(adminService.getById(id));
    }

    @GetMapping
    public ApiResponse<List<Admin>> getAll() {
        return ApiResponse.success(adminService.getAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<Admin> update(@PathVariable("id") Long id, @Valid @RequestBody AdminRequest request) {
        return ApiResponse.success("Admin updated", adminService.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivate(@PathVariable("id") Long id) {
        adminService.deactivate(id);
        return ApiResponse.success("Admin deactivated", null);
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activate(@PathVariable("id") Long id) {
        adminService.activate(id);
        return ApiResponse.success("Admin activated", null);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        adminService.delete(id);
    }
}