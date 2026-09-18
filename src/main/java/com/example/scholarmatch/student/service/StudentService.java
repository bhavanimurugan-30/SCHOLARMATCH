package com.example.scholarmatch.student.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.student.dto.StudentRequest;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student register(StudentRequest request) {

        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "A student with this email already exists"
            );
        }

        if (studentRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new IllegalArgumentException(
                    "A student with this mobile number already exists"
            );
        }

        Student student = new Student();

        student.setFullName(request.getFullName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setMobileNumber(request.getMobileNumber());
        student.setEmail(request.getEmail());
        student.setPasswordHash(hashPassword(request.getPassword()));
        student.setCategory(request.getCategory());
        student.setState(request.getState());
        student.setDistrict(request.getDistrict());

        student.setAadhaarNumber(
                request.getAadhaarNumber() == null ||
                        request.getAadhaarNumber().isBlank()
                        ? null
                        : request.getAadhaarNumber()
        );

        student.setPermanentAddress(request.getPermanentAddress());
        student.setReligion(request.getReligion());
        student.setPwd(request.isPwd());
        student.setDisabilityPercentage(
                request.getDisabilityPercentage()
        );
        student.setAnnualIncome(request.getAnnualIncome());
        student.setMarksCgpa(request.getMarksCgpa());
        student.setAccountStatus("ACTIVE");

        return studentRepository.save(student);
    }

    public Student getById(Long studentId) {

        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        )
                );
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    // UPDATE PROFILE
    public Student update(Long studentId, StudentRequest request) {

        Student existing = getById(studentId);

        if (request.getFullName() != null &&
                !request.getFullName().isBlank()) {
            existing.setFullName(request.getFullName());
        }

        // LocalDate - no isBlank() or parse()
        if (request.getDateOfBirth() != null) {
            existing.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getGender() != null &&
                !request.getGender().isBlank()) {
            existing.setGender(request.getGender());
        }

        if (request.getMobileNumber() != null &&
                !request.getMobileNumber().isBlank()) {
            existing.setMobileNumber(request.getMobileNumber());
        }

        if (request.getEmail() != null &&
                !request.getEmail().isBlank()) {
            existing.setEmail(request.getEmail());
        }

        if (request.getCategory() != null &&
                !request.getCategory().isBlank()) {
            existing.setCategory(request.getCategory());
        }

        if (request.getState() != null &&
                !request.getState().isBlank()) {
            existing.setState(request.getState());
        }

        if (request.getDistrict() != null &&
                !request.getDistrict().isBlank()) {
            existing.setDistrict(request.getDistrict());
        }

        if (request.getAadhaarNumber() != null &&
                !request.getAadhaarNumber().isBlank()) {
            existing.setAadhaarNumber(request.getAadhaarNumber());
        }

        if (request.getPermanentAddress() != null &&
                !request.getPermanentAddress().isBlank()) {
            existing.setPermanentAddress(
                    request.getPermanentAddress()
            );
        }

        if (request.getReligion() != null &&
                !request.getReligion().isBlank()) {
            existing.setReligion(request.getReligion());
        }

        if (request.getDisabilityPercentage() != null) {
            existing.setDisabilityPercentage(
                    request.getDisabilityPercentage()
            );
        }

        if (request.getAnnualIncome() != null) {
            existing.setAnnualIncome(request.getAnnualIncome());
        }

        if (request.getMarksCgpa() != null) {
            existing.setMarksCgpa(request.getMarksCgpa());
        }

        existing.setPwd(request.isPwd());

        if (request.getProfilePhotoUrl() != null && !request.getProfilePhotoUrl().isBlank()) {
            existing.setProfilePhotoUrl(request.getProfilePhotoUrl());
        }

        if (request.getDomicile() != null) {
            existing.setDomicile(request.getDomicile());
        }

        if (request.getIncomeCategory() != null) {
            existing.setIncomeCategory(request.getIncomeCategory());
        }

        // Password update only when new password is provided
        if (request.getPassword() != null &&
                !request.getPassword().isBlank()) {

            existing.setPasswordHash(
                    hashPassword(request.getPassword())
            );
        }

        studentRepository.update(studentId, existing);

        return getById(studentId);
    }

    public Student updateProfilePhoto(Long studentId, String photoUrl) {
        getById(studentId);
        studentRepository.updateProfilePhoto(studentId, photoUrl);
        return getById(studentId);
    }

    public void suspend(Long studentId) {

        getById(studentId);

        studentRepository.updateAccountStatus(
                studentId,
                "SUSPENDED"
        );
    }

    public void activate(Long studentId) {

        getById(studentId);

        studentRepository.updateAccountStatus(
                studentId,
                "ACTIVE"
        );
    }

    public void delete(Long studentId) {

        getById(studentId);

        studentRepository.deleteById(studentId);
    }

    private String hashPassword(String rawPassword) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawPassword.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "Password hashing failed",
                    e
            );
        }
    }
}