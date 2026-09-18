package com.example.scholarmatch.adminactivitylog.controller;

import com.example.scholarmatch.adminactivitylog.dto.AdminActivityLogRequest;
import com.example.scholarmatch.adminactivitylog.model.AdminActivityLog;
import com.example.scholarmatch.adminactivitylog.service.AdminActivityLogService;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin-activity-log")
public class AdminActivityLogController {

    private final AdminActivityLogService activityLogService;

    public AdminActivityLogController(AdminActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @PostMapping
    public ApiResponse<AdminActivityLog> record(@Valid @RequestBody AdminActivityLogRequest request) {
        return ApiResponse.success("Activity logged", activityLogService.record(request));
    }

    @GetMapping("/admin/{adminId}")
    public ApiResponse<List<AdminActivityLog>> getByAdmin(@PathVariable Long adminId) {
        return ApiResponse.success(activityLogService.getByAdminId(adminId));
    }

    @GetMapping("/target")
    public ApiResponse<List<AdminActivityLog>> getByTarget(
            @RequestParam("entity") String targetEntity,
            @RequestParam("id") Long targetId) {
        return ApiResponse.success(activityLogService.getByTarget(targetEntity, targetId));
    }

    @GetMapping
    public ApiResponse<List<AdminActivityLog>> getAll() {
        return ApiResponse.success(activityLogService.getAll());
    }
}