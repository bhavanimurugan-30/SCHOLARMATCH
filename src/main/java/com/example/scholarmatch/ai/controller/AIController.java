package com.example.scholarmatch.ai.controller;

import com.example.scholarmatch.ai.dto.ScholarshipExtractionRequest;
import com.example.scholarmatch.ai.service.AIService;
import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.scholarship.dto.ScholarshipRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/extract-scholarship")
    public ApiResponse<ScholarshipRequest> extractScholarship(@Valid @RequestBody ScholarshipExtractionRequest request) {
        return ApiResponse.success("Extracted scholarship details - review before saving",
                aiService.extractScholarshipDetails(request.getDescription()));
    }
}