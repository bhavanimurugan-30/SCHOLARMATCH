package com.example.scholarmatch.student.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.student.dto.StudentRequest;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final com.example.scholarmatch.filestorage.service.FileStorageService fileStorageService;

    public StudentController(StudentService studentService,
                             com.example.scholarmatch.filestorage.service.FileStorageService fileStorageService) {
        this.studentService = studentService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ApiResponse<Student> register(
            @Valid @RequestBody StudentRequest request) {

        Student created = studentService.register(request);

        return ApiResponse.success(
                "Student registered successfully",
                created
        );
    }

    /**
     * Serves the student's profile photo. Authenticated on purpose: the uploads folder is
     * NOT exposed as a static resource, so photos cannot be read without a valid token.
     */
    @GetMapping("/{id}/photo")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> getPhoto(
            @PathVariable("id") Long id) {

        Student student = studentService.getById(id);
        String path = student.getProfilePhotoUrl();
        if (path == null || path.isBlank()) {
            throw new com.example.scholarmatch.exception.ResourceNotFoundException(
                    "No profile photo uploaded for student id: " + id);
        }

        org.springframework.core.io.Resource resource;
        try {
            resource = fileStorageService.loadAsResource(path);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            throw new com.example.scholarmatch.exception.ResourceNotFoundException(
                    "Profile photo file is missing on the server. Please upload it again.");
        }

        String lower = path.toLowerCase();
        String contentType = lower.endsWith(".png") ? "image/png" : "image/jpeg";

        return org.springframework.http.ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"profile-photo\"")
                .body(resource);
    }

    @GetMapping("/{id}")
    public ApiResponse<Student> getById(
            @PathVariable("id") Long id) {

        return ApiResponse.success(
                studentService.getById(id)
        );
    }

    @GetMapping
    public ApiResponse<List<Student>> getAll() {

        return ApiResponse.success(
                studentService.getAll()
        );
    }

    // UPDATE PROFILE
    // IMPORTANT: @Valid removed here
    @PutMapping("/{id}")
    public ApiResponse<Student> update(
            @PathVariable("id") Long id,
            @RequestBody StudentRequest request) {

        return ApiResponse.success(
                "Student updated successfully",
                studentService.update(id, request)
        );
    }

    @PostMapping(value = "/{id}/photo", consumes = "multipart/form-data")
    public ApiResponse<Student> uploadPhoto(
            @PathVariable("id") Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        com.example.scholarmatch.filestorage.service.FileStorageService.StoredFile stored =
                fileStorageService.store("profile-photos/" + id, file);
        return ApiResponse.success("Profile photo uploaded", studentService.updateProfilePhoto(id, stored.getRelativePath()));
    }

    @PatchMapping("/{id}/suspend")
    public ApiResponse<Void> suspend(
            @PathVariable("id") Long id) {

        studentService.suspend(id);

        return ApiResponse.success(
                "Student suspended",
                null
        );
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activate(
            @PathVariable("id") Long id) {

        studentService.activate(id);

        return ApiResponse.success(
                "Student activated",
                null
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id) {

        studentService.delete(id);
    }
}