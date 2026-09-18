package com.example.scholarmatch.passwordreset.service;

import com.example.scholarmatch.institution.model.Institution;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.passwordreset.model.PasswordResetToken;
import com.example.scholarmatch.passwordreset.repository.PasswordResetTokenRepository;
import com.example.scholarmatch.security.PasswordHashUtil;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private final StudentRepository studentRepository;
    private final InstitutionRepository institutionRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.reset-password.base-url:http://localhost:5500/frontend/reset-password.html}")
    private String resetBaseUrl;

    @Value("${app.reset-password.token-expiry-minutes:30}")
    private int tokenExpiryMinutes;

    public PasswordResetService(StudentRepository studentRepository,
                                InstitutionRepository institutionRepository,
                                PasswordResetTokenRepository tokenRepository,
                                JavaMailSender mailSender) {
        this.studentRepository = studentRepository;
        this.institutionRepository = institutionRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
    }

    public void forgotPassword(String email, String role) {
        boolean accountExists = "STUDENT".equalsIgnoreCase(role)
                ? studentRepository.findByEmail(email).isPresent()
                : institutionRepository.findByEmail(email).isPresent();

        if (!accountExists) {
            return;
        }

        String normalizedRole = role.toUpperCase();
        tokenRepository.invalidateActiveTokensForEmail(email, normalizedRole);

        String rawToken = generateSecureToken();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setEmail(email);
        resetToken.setAccountRole(normalizedRole);
        resetToken.setToken(rawToken);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(tokenExpiryMinutes));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);

        sendResetEmail(email, rawToken);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("This reset link has already been used");
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This reset link has expired");
        }

        String newHash = PasswordHashUtil.hash(newPassword);

        if ("STUDENT".equalsIgnoreCase(resetToken.getAccountRole())) {
            Student student = studentRepository.findByEmail(resetToken.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Account no longer exists"));
            studentRepository.updatePasswordHash(student.getStudentId(), newHash);
        } else {
            Institution institution = institutionRepository.findByEmail(resetToken.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Account no longer exists"));
            institutionRepository.updatePasswordHash(institution.getInstitutionId(), newHash);
        }

        tokenRepository.markUsed(resetToken.getTokenId());
    }

    private String generateSecureToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private void sendResetEmail(String toEmail, String rawToken) {
        try {
            String resetLink = resetBaseUrl + "?token=" + rawToken;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Reset your ScholarMatch password");
            message.setText("We received a request to reset your ScholarMatch password. " +
                    "Click the link below to set a new password. This link expires in " +
                    tokenExpiryMinutes + " minutes and can only be used once.\n\n" +
                    resetLink + "\n\nIf you did not request this, you can ignore this email.");
            mailSender.send(message);
        } catch (Exception ignored) {
            // forgotPassword always responds the same way regardless of mail outcome
        }
    }
}