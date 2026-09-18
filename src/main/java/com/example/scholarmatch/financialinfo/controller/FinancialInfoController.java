package com.example.scholarmatch.financialinfo.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.financialinfo.dto.FinancialInfoRequest;
import com.example.scholarmatch.financialinfo.model.FinancialInfo;
import com.example.scholarmatch.financialinfo.service.FinancialInfoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/{studentId}/financial-info")
public class FinancialInfoController {

    private final FinancialInfoService financialInfoService;

    public FinancialInfoController(FinancialInfoService financialInfoService) {
        this.financialInfoService = financialInfoService;
    }

    @PostMapping
    public ApiResponse<FinancialInfo> create(@PathVariable Long studentId,
                                             @Valid @RequestBody FinancialInfoRequest request) {
        return ApiResponse.success("Financial info created", financialInfoService.create(studentId, request));
    }

    @GetMapping
    public ApiResponse<FinancialInfo> get(@PathVariable Long studentId) {
        return ApiResponse.success(financialInfoService.getByStudentId(studentId));
    }

    @PutMapping
    public ApiResponse<FinancialInfo> update(@PathVariable Long studentId,
                                             @Valid @RequestBody FinancialInfoRequest request) {
        return ApiResponse.success("Financial info updated", financialInfoService.update(studentId, request));
    }

    @DeleteMapping
    public ApiResponse<Void> delete(@PathVariable Long studentId) {
        financialInfoService.delete(studentId);
        return ApiResponse.success("Financial info deleted", null);
    }
}