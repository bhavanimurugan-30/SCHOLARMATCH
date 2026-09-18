package com.example.scholarmatch.scholarshiprequireddocument.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.scholarshiprequireddocument.dto.ScholarshipRequiredDocumentRequest;
import com.example.scholarmatch.scholarshiprequireddocument.model.ScholarshipRequiredDocument;
import com.example.scholarmatch.scholarshiprequireddocument.service.ScholarshipRequiredDocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholarships/{scholarshipId}/required-documents")
public class ScholarshipRequiredDocumentController {

    private final ScholarshipRequiredDocumentService requiredDocumentService;

    public ScholarshipRequiredDocumentController(ScholarshipRequiredDocumentService requiredDocumentService) {
        this.requiredDocumentService = requiredDocumentService;
    }

    @PostMapping
    public ApiResponse<ScholarshipRequiredDocument> add(@PathVariable Long scholarshipId,
                                                        @Valid @RequestBody ScholarshipRequiredDocumentRequest request) {
        return ApiResponse.success("Required document added", requiredDocumentService.add(scholarshipId, request));
    }

    @GetMapping
    public ApiResponse<List<ScholarshipRequiredDocument>> getAll(@PathVariable Long scholarshipId) {
        return ApiResponse.success(requiredDocumentService.getByScholarshipId(scholarshipId));
    }

    @DeleteMapping("/{certificateTypeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long scholarshipId, @PathVariable Long certificateTypeId) {
        requiredDocumentService.remove(scholarshipId, certificateTypeId);
    }
}