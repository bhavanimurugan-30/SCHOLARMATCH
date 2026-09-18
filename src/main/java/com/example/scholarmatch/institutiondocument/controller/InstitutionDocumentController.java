package com.example.scholarmatch.institutiondocument.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.filestorage.service.FileStorageService;
import com.example.scholarmatch.institutiondocument.dto.InstitutionDocumentRequest;
import com.example.scholarmatch.institutiondocument.model.InstitutionDocument;
import com.example.scholarmatch.institutiondocument.service.InstitutionDocumentService;
import com.example.scholarmatch.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/institutions/{institutionId}/documents")
@Validated
public class InstitutionDocumentController {

    private final InstitutionDocumentService institutionDocumentService;
    private final FileStorageService fileStorageService;

    public InstitutionDocumentController(InstitutionDocumentService institutionDocumentService,
                                         FileStorageService fileStorageService) {
        this.institutionDocumentService = institutionDocumentService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ApiResponse<InstitutionDocument> upload(@PathVariable Long institutionId,
                                                   @Valid @RequestBody InstitutionDocumentRequest request) {
        return ApiResponse.success("Document uploaded", institutionDocumentService.upload(institutionId, request));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<InstitutionDocument> uploadFile(
            @PathVariable Long institutionId,
            @RequestParam("documentType")
            @Pattern(regexp = "REGISTRATION_CERTIFICATE|PAN|AUTHORIZATION_LETTER", message = "Invalid document type")
            String documentType,
            @RequestParam("file") MultipartFile file) {

        FileStorageService.StoredFile stored = fileStorageService.store(
                "institution-documents/" + institutionId, file);

        InstitutionDocumentRequest request = new InstitutionDocumentRequest();
        request.setDocumentType(documentType);
        request.setFilePath(stored.getRelativePath());

        return ApiResponse.success("Document uploaded", institutionDocumentService.upload(institutionId, request));
    }

    @GetMapping
    public ApiResponse<List<InstitutionDocument>> getAll(@PathVariable Long institutionId) {
        requireOwnerOrAdmin(institutionId);
        return ApiResponse.success(institutionDocumentService.getByInstitutionId(institutionId));
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long institutionId, @PathVariable Long documentId) {
        requireOwnerOrAdmin(institutionId);
        InstitutionDocument doc = institutionDocumentService.getById(documentId);
        if (!doc.getInstitutionId().equals(institutionId)) {
            throw new ResourceNotFoundException("Document not found with id: " + documentId);
        }
        institutionDocumentService.delete(documentId);
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<Resource> viewFile(@PathVariable Long institutionId, @PathVariable Long documentId) {
        requireOwnerOrAdmin(institutionId);
        InstitutionDocument doc = institutionDocumentService.getById(documentId);
        if (!doc.getInstitutionId().equals(institutionId)) {
            throw new ResourceNotFoundException("Document not found with id: " + documentId);
        }

        Resource resource;
        try {
            resource = fileStorageService.loadAsResource(doc.getFilePath());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            throw new ResourceNotFoundException(
                    "Document file is missing on the server. Please re-upload this document.");
        }
        String contentType = resource.getFilename() != null && resource.getFilename().toLowerCase().endsWith(".pdf")
                ? "application/pdf" : "image/jpeg";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Allows an ADMIN (reviewing documents), or the INSTITUTION whose own id
     * matches the path variable, to proceed. Anyone else is rejected with 403.
     * Upload endpoints intentionally skip this check: a newly-registered
     * institution is PENDING and cannot log in yet (see AuthController /
     * JwtAuthenticationFilter), so it has no JWT when it first submits its
     * verification documents.
     */
    private void requireOwnerOrAdmin(Long institutionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal principal) {
            boolean isAdmin = "ADMIN".equals(principal.getRole());
            boolean isOwner = "INSTITUTION".equals(principal.getRole()) && principal.getId().equals(institutionId);
            if (isAdmin || isOwner) {
                return;
            }
        }
        throw new AccessDeniedException("You are not allowed to access this institution's documents");
    }
}