package com.example.scholarmatch.scholarshipviewlog.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.scholarshipviewlog.model.ScholarshipViewLog;
import com.example.scholarmatch.scholarshipviewlog.service.ScholarshipViewLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scholarships/{scholarshipId}/views")
public class ScholarshipViewLogController {

    private final ScholarshipViewLogService viewLogService;

    public ScholarshipViewLogController(ScholarshipViewLogService viewLogService) {
        this.viewLogService = viewLogService;
    }

    @PostMapping
    public ApiResponse<ScholarshipViewLog> record(
            @PathVariable Long scholarshipId,
            @RequestParam(value = "studentId", required = false) Long studentId) {
        return ApiResponse.success("View recorded", viewLogService.record(scholarshipId, studentId));
    }

    @GetMapping
    public ApiResponse<List<ScholarshipViewLog>> getAll(@PathVariable Long scholarshipId) {
        return ApiResponse.success(viewLogService.getByScholarshipId(scholarshipId));
    }
    @GetMapping("/most-viewed")
    public ApiResponse<List<Map<String, Object>>> mostViewed(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ApiResponse.success(viewLogService.getMostViewed(limit));
    }
}