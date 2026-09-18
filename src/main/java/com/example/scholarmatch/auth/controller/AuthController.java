package com.example.scholarmatch.auth.controller;

import com.example.scholarmatch.admin.model.Admin;
import com.example.scholarmatch.admin.repository.AdminRepository;
import com.example.scholarmatch.auth.dto.LoginRequest;
import com.example.scholarmatch.auth.dto.LoginResponse;
import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.institution.model.Institution;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.security.JwtUtil;
import com.example.scholarmatch.security.PasswordHashUtil;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.scholarmatch.passwordreset.dto.ForgotPasswordRequest;
import com.example.scholarmatch.passwordreset.dto.ResetPasswordRequest;
import com.example.scholarmatch.passwordreset.service.PasswordResetService;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final InstitutionRepository institutionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;
    public AuthController(StudentRepository studentRepository,
                          AdminRepository adminRepository,
                          InstitutionRepository institutionRepository,
                          JwtUtil jwtUtil,
                          PasswordResetService passwordResetService) {
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.institutionRepository = institutionRepository;
        this.jwtUtil = jwtUtil;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/student/login")
    public ResponseEntity<ApiResponse<LoginResponse>> studentLogin(@Valid @RequestBody LoginRequest request) {
        Student student = studentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!PasswordHashUtil.matches(request.getPassword(), student.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!"ACTIVE".equalsIgnoreCase(student.getAccountStatus())) {
            throw new BadCredentialsException("Account is not active");
        }

        String token = jwtUtil.generateToken(student.getStudentId(), student.getEmail(), "STUDENT");
        LoginResponse body = new LoginResponse(token, "STUDENT", student.getStudentId(),
                student.getEmail(), student.getFullName());
        return ResponseEntity.ok(ApiResponse.success(body));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<LoginResponse>> adminLogin(@Valid @RequestBody LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!PasswordHashUtil.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!admin.isActive()) {
            throw new BadCredentialsException("Admin account is not active");
        }

        String token = jwtUtil.generateToken(admin.getAdminId(), admin.getEmail(), "ADMIN");
        LoginResponse body = new LoginResponse(token, "ADMIN", admin.getAdminId(),
                admin.getEmail(), admin.getFullName());
        return ResponseEntity.ok(ApiResponse.success(body));
    }

    @PostMapping("/institution/login")
    public ResponseEntity<ApiResponse<LoginResponse>> institutionLogin(@Valid @RequestBody LoginRequest request) {
        Institution institution = institutionRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!PasswordHashUtil.matches(request.getPassword(), institution.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!"APPROVED".equalsIgnoreCase(institution.getVerificationStatus())) {
            throw new BadCredentialsException(
                    "Institution is not verified yet (status: " + institution.getVerificationStatus() + ")");
        }

        String token = jwtUtil.generateToken(institution.getInstitutionId(), institution.getEmail(), "INSTITUTION");
        LoginResponse body = new LoginResponse(token, "INSTITUTION", institution.getInstitutionId(),
                institution.getEmail(), institution.getInstitutionName());
        return ResponseEntity.ok(ApiResponse.success(body));
    }
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.getEmail(), request.getRole());
        return ApiResponse.success("If an account exists for this email, a reset link has been sent.", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.success("Password has been reset successfully.", null);
    }
}