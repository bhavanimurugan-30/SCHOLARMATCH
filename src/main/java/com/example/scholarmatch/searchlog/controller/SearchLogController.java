package com.example.scholarmatch.searchlog.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.searchlog.dto.SearchLogRequest;
import com.example.scholarmatch.searchlog.model.SearchLog;
import com.example.scholarmatch.searchlog.service.SearchLogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-log")
public class SearchLogController {

    private final SearchLogService searchLogService;

    public SearchLogController(SearchLogService searchLogService) {
        this.searchLogService = searchLogService;
    }

    @PostMapping
    public ApiResponse<SearchLog> record(@Valid @RequestBody SearchLogRequest request) {
        return ApiResponse.success("Search logged", searchLogService.record(request));
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<SearchLog>> getByStudent(@PathVariable Long studentId) {
        return ApiResponse.success(searchLogService.getByStudentId(studentId));
    }
    @GetMapping("/recent")
    public ApiResponse<List<SearchLog>> recent(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ApiResponse.success(searchLogService.getRecent(limit));
    }
}