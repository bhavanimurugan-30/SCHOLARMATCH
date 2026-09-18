package com.example.scholarmatch.academicinfo.controller;
import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.academicinfo.dto.AcademicInfoRequest;
import com.example.scholarmatch.academicinfo.model.AcademicInfo;
import com.example.scholarmatch.academicinfo.service.AcademicInfoService;
import com.example.scholarmatch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students/{studentId}/academic-info")
public class AcademicInfoController {

    private final AcademicInfoService academicInfoService;

    public AcademicInfoController(AcademicInfoService academicInfoService) {
        this.academicInfoService = academicInfoService;
    }

    @PostMapping
    public ApiResponse<AcademicInfo> create(@PathVariable Long studentId,
                                            @Valid @RequestBody AcademicInfoRequest request) {
        return ApiResponse.success("Academic info created", academicInfoService.create(studentId, request));
    }

    @GetMapping
    public ApiResponse<AcademicInfo> get(@PathVariable Long studentId) {
        return ApiResponse.success(academicInfoService.getByStudentId(studentId));
    }

    @PutMapping
    public ApiResponse<AcademicInfo> update(@PathVariable Long studentId,
                                            @Valid @RequestBody AcademicInfoRequest request) {
        return ApiResponse.success("Academic info updated", academicInfoService.update(studentId, request));
    }

    @DeleteMapping
    public ApiResponse<Void> delete(@PathVariable Long studentId) {
        academicInfoService.delete(studentId);
        return ApiResponse.success("Academic info deleted", null);
    }
}