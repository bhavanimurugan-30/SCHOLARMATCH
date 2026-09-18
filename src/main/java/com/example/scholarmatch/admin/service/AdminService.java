package com.example.scholarmatch.admin.service;

import com.example.scholarmatch.admin.dto.AdminRequest;
import com.example.scholarmatch.admin.model.Admin;
import com.example.scholarmatch.admin.repository.AdminRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Admin create(AdminRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An admin with this email already exists");
        }

        Admin admin = new Admin();
        admin.setFullName(request.getFullName());
        admin.setEmail(request.getEmail());
        admin.setPasswordHash(hashPassword(request.getPassword()));
        admin.setRole(request.getRole());
        admin.setActive(true);

        return adminRepository.save(admin);
    }

    public Admin getById(Long adminId) {
        return adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));
    }

    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    public Admin update(Long adminId, AdminRequest request) {
        Admin existing = getById(adminId);
        existing.setFullName(request.getFullName());
        existing.setEmail(request.getEmail());
        existing.setRole(request.getRole());

        adminRepository.update(adminId, existing);
        return getById(adminId);
    }

    public void deactivate(Long adminId) {
        getById(adminId);
        adminRepository.updateActiveStatus(adminId, false);
    }

    public void activate(Long adminId) {
        getById(adminId);
        adminRepository.updateActiveStatus(adminId, true);
    }

    public void delete(Long adminId) {
        getById(adminId);
        adminRepository.deleteById(adminId);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}