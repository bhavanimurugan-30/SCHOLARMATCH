package com.example.scholarmatch.studentdocument.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.filestorage.service.FileStorageService;
import com.example.scholarmatch.security.CustomUserPrincipal;
import com.example.scholarmatch.studentdocument.dto.StudentDocumentRequest;
import com.example.scholarmatch.studentdocument.model.StudentDocument;
import com.example.scholarmatch.studentdocument.service.StudentDocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/documents")
public class StudentDocumentController {

    private final StudentDocumentService studentDocumentService;
    private final FileStorageService fileStorageService;

    public StudentDocumentController(StudentDocumentService studentDocumentService,
                                     FileStorageService fileStorageService) {
        this.studentDocumentService = studentDocumentService;
        this.fileStorageService = fileStorageService;
    }

    /** A STUDENT-role caller may only act on their own studentId; ADMIN passes through untouched. */
    private void assertOwnership(Long studentId, CustomUserPrincipal principal) {
        if (principal != null && "STUDENT".equals(principal.getRole()) && !studentId.equals(principal.getId())) {
            throw new AccessDeniedException("You can only access your own documents");
        }
    }

    @PostMapping
    public ApiResponse<StudentDocument> upload(@PathVariable Long studentId,
                                               @AuthenticationPrincipal CustomUserPrincipal principal,
                                               @Valid @RequestBody StudentDocumentRequest request) {
        assertOwnership(studentId, principal);
        return ApiResponse.success("Document uploaded", studentDocumentService.upload(studentId, request));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ApiResponse<StudentDocument> uploadFile(@PathVariable Long studentId,
                                                   @AuthenticationPrincipal CustomUserPrincipal principal,
                                                   @RequestParam("certificateTypeId") Long certificateTypeId,
                                                   @RequestParam(value = "issueDate", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issueDate,
                                                   @RequestParam(value = "expiryDate", required = false)
                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
                                                   @RequestParam("file") MultipartFile file) {
        assertOwnership(studentId, principal);

        FileStorageService.StoredFile stored = fileStorageService.store("student-documents/" + studentId, file);

        StudentDocumentRequest request = new StudentDocumentRequest();
        request.setCertificateTypeId(certificateTypeId);
        request.setFilePath(stored.getRelativePath());
        request.setOriginalFileName(stored.getOriginalFileName());
        request.setContentType(stored.getContentType());
        request.setFileSizeBytes(stored.getSizeBytes());
        request.setIssueDate(issueDate);
        request.setExpiryDate(expiryDate);

        return ApiResponse.success("Document uploaded", studentDocumentService.upload(studentId, request));
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long studentId,
                                                 @PathVariable Long documentId,
                                                 @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);

        StudentDocument doc = studentDocumentService.getById(documentId);
        if (!doc.getStudentId().equals(studentId)) {
            throw new ResourceNotFoundException("Document not found with id: " + documentId);
        }

        Resource resource;
        try {
            resource = fileStorageService.loadAsResource(doc.getFilePath());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            throw new ResourceNotFoundException(
                    "Document file is missing on the server. Please re-upload this document.");
        }
        String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        String downloadName = doc.getOriginalFileName() != null ? doc.getOriginalFileName() : resource.getFilename();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + downloadName + "\"")
                .body(resource);
    }

    @GetMapping
    public ApiResponse<List<StudentDocument>> getAll(@PathVariable Long studentId,
                                                     @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success(studentDocumentService.getByStudentId(studentId));
    }

    @GetMapping("/{documentId}")
    public ApiResponse<StudentDocument> getById(@PathVariable Long studentId,
                                                @PathVariable Long documentId,
                                                @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        StudentDocument doc = studentDocumentService.getById(documentId);
        if (!doc.getStudentId().equals(studentId)) {
            throw new ResourceNotFoundException("Document not found with id: " + documentId);
        }
        return ApiResponse.success(doc);
    }

    @PutMapping("/{documentId}")
    public ApiResponse<StudentDocument> update(@PathVariable Long studentId,
                                               @PathVariable Long documentId,
                                               @AuthenticationPrincipal CustomUserPrincipal principal,
                                               @Valid @RequestBody StudentDocumentRequest request) {
        assertOwnership(studentId, principal);
        return ApiResponse.success("Document updated", studentDocumentService.update(documentId, request));
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long studentId,
                       @PathVariable Long documentId,
                       @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        studentDocumentService.delete(documentId);
    }
}