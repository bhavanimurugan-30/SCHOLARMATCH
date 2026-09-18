package com.example.scholarmatch.specialstatus.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.specialstatus.dto.SpecialStatusRequest;
import com.example.scholarmatch.specialstatus.model.SpecialStatus;
import com.example.scholarmatch.specialstatus.service.SpecialStatusService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/{studentId}/special-status")
public class SpecialStatusController {

    private final SpecialStatusService specialStatusService;

    public SpecialStatusController(SpecialStatusService specialStatusService) {
        this.specialStatusService = specialStatusService;
    }

    @PostMapping
    public ApiResponse<SpecialStatus> create(@PathVariable Long studentId,
                                             @Valid @RequestBody SpecialStatusRequest request) {
        return ApiResponse.success("Special status created", specialStatusService.create(studentId, request));
    }

    @GetMapping
    public ApiResponse<SpecialStatus> get(@PathVariable Long studentId) {
        return ApiResponse.success(specialStatusService.getByStudentId(studentId));
    }

    @PutMapping
    public ApiResponse<SpecialStatus> update(@PathVariable Long studentId,
                                             @Valid @RequestBody SpecialStatusRequest request) {
        return ApiResponse.success("Special status updated", specialStatusService.update(studentId, request));
    }

    @DeleteMapping
    public ApiResponse<Void> delete(@PathVariable Long studentId) {
        specialStatusService.delete(studentId);
        return ApiResponse.success("Special status deleted", null);
    }
}