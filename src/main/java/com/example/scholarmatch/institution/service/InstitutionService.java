package com.example.scholarmatch.institution.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.dto.InstitutionRequest;
import com.example.scholarmatch.institution.model.Institution;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.institutionverificationlog.service.InstitutionVerificationLogService;
import com.example.scholarmatch.notification.service.NotificationService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final InstitutionVerificationLogService verificationLogService;
    private final NotificationService notificationService;

    public InstitutionService(
            InstitutionRepository institutionRepository,
            InstitutionVerificationLogService verificationLogService,
            NotificationService notificationService) {

        this.institutionRepository = institutionRepository;
        this.verificationLogService = verificationLogService;
        this.notificationService = notificationService;
    }

    /**
     * Registers a new institution.
     *
     * New institution registration generates
     * an ADMIN notification.
     */
    public Institution register(
            InstitutionRequest request) {

        if (institutionRepository.existsByEmail(
                request.getEmail())) {

            throw new IllegalArgumentException(
                    "An institution with this email already exists"
            );
        }

        if (request.getRegistrationNumber() != null
                && institutionRepository
                .existsByRegistrationNumber(
                        request.getRegistrationNumber()
                )) {

            throw new IllegalArgumentException(
                    "An institution with this registration number already exists"
            );
        }

        Institution institution =
                new Institution();

        institution.setInstitutionName(
                request.getInstitutionName()
        );

        institution.setInstitutionType(
                request.getInstitutionType()
        );

        institution.setEmail(
                request.getEmail()
        );

        institution.setPasswordHash(
                hashPassword(
                        request.getPassword()
                )
        );

        institution.setWebsiteUrl(
                request.getWebsiteUrl()
        );

        institution.setState(
                request.getState()
        );

        institution.setDistrict(
                request.getDistrict()
        );

        institution.setPanNumber(
                request.getPanNumber()
        );

        institution.setRegistrationNumber(
                request.getRegistrationNumber()
        );

        institution.setVerificationStatus(
                "PENDING"
        );

        Institution saved =
                institutionRepository.save(
                        institution
                );

        /*
         * Create the initial verification log.
         */
        verificationLogService.recordSubmission(
                saved.getInstitutionId()
        );

        /*
         * Notify ADMIN about the new institution.
         */
        notificationService.notifyAdmin(
                "INSTITUTION_REGISTERED",
                null,
                "New institution registration",
                "New institution registration: "
                        + saved.getInstitutionName()
                        + "."
        );

        return saved;
    }

    public Institution getById(
            Long institutionId) {

        return institutionRepository.findById(
                        institutionId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );
    }

    public List<Institution> getAll() {

        return institutionRepository.findAll();
    }

    public List<Institution> getByVerificationStatus(
            String status) {

        return institutionRepository
                .findByVerificationStatus(status);
    }

    public Institution update(
            Long institutionId,
            InstitutionRequest request) {

        Institution existing =
                getById(institutionId);

        existing.setInstitutionName(
                request.getInstitutionName()
        );

        existing.setInstitutionType(
                request.getInstitutionType()
        );

        existing.setWebsiteUrl(
                request.getWebsiteUrl()
        );

        existing.setState(
                request.getState()
        );

        existing.setDistrict(
                request.getDistrict()
        );

        existing.setPanNumber(
                request.getPanNumber()
        );

        existing.setRegistrationNumber(
                request.getRegistrationNumber()
        );

        institutionRepository.update(
                institutionId,
                existing
        );

        return getById(institutionId);
    }

    public void delete(
            Long institutionId) {

        getById(institutionId);

        institutionRepository.deleteById(
                institutionId
        );
    }

    private String hashPassword(
            String rawPassword) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            rawPassword.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(
                    "Password hashing failed",
                    e
            );
        }
    }
}

