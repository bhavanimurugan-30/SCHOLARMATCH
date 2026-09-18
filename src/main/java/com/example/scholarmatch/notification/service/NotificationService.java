package com.example.scholarmatch.notification.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.notification.dto.NotificationRequest;
import com.example.scholarmatch.notification.model.Notification;
import com.example.scholarmatch.notification.repository.NotificationRepository;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final InstitutionRepository institutionRepository;
    private final ScholarshipRepository scholarshipRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            StudentRepository studentRepository,
            InstitutionRepository institutionRepository,
            ScholarshipRepository scholarshipRepository) {

        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.institutionRepository = institutionRepository;
        this.scholarshipRepository = scholarshipRepository;
    }

    public Notification create(NotificationRequest request) {

        Notification n = new Notification();

        n.setRecipientType(request.getRecipientType());
        n.setNotificationType(request.getNotificationType());
        n.setTitle(request.getTitle());
        n.setMessage(request.getMessage());
        n.setRead(false);
        n.setEmailSent(false);

        switch (request.getRecipientType()) {

            case "STUDENT" -> {

                studentRepository.findById(request.getRecipientId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found with id: "
                                                + request.getRecipientId()
                                )
                        );

                n.setStudentId(request.getRecipientId());
            }

            case "INSTITUTION" -> {

                institutionRepository.findById(request.getRecipientId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Institution not found with id: "
                                                + request.getRecipientId()
                                )
                        );

                n.setInstitutionId(request.getRecipientId());
            }

            case "ADMIN" -> {
                // ADMIN notifications are shared.
                // No admin ID is stored in notification table.
                n.setStudentId(null);
                n.setInstitutionId(null);
            }

            default ->
                    throw new IllegalArgumentException(
                            "Invalid recipient type: "
                                    + request.getRecipientType()
                    );
        }

        if (request.getRelatedScholarshipId() != null) {

            scholarshipRepository.findById(
                            request.getRelatedScholarshipId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Scholarship not found with id: "
                                            + request.getRelatedScholarshipId()
                            )
                    );

            n.setRelatedScholarshipId(
                    request.getRelatedScholarshipId()
            );
        }

        return notificationRepository.save(n);
    }

    public Notification notifyStudent(
            Long studentId,
            String type,
            Long scholarshipId,
            String title,
            String message) {

        Notification n = new Notification();

        n.setRecipientType("STUDENT");
        n.setStudentId(studentId);
        n.setNotificationType(type);
        n.setRelatedScholarshipId(scholarshipId);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setEmailSent(false);

        return notificationRepository.save(n);
    }

    public Notification notifyInstitution(
            Long institutionId,
            String type,
            Long scholarshipId,
            String title,
            String message) {

        Notification n = new Notification();

        n.setRecipientType("INSTITUTION");
        n.setInstitutionId(institutionId);
        n.setNotificationType(type);
        n.setRelatedScholarshipId(scholarshipId);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setEmailSent(false);

        return notificationRepository.save(n);
    }

    public Notification notifyAdmin(
            String type,
            Long scholarshipId,
            String title,
            String message) {

        Notification n = new Notification();

        n.setRecipientType("ADMIN");
        n.setStudentId(null);
        n.setInstitutionId(null);
        n.setNotificationType(type);
        n.setRelatedScholarshipId(scholarshipId);
        n.setTitle(title);
        n.setMessage(message);
        n.setRead(false);
        n.setEmailSent(false);

        return notificationRepository.save(n);
    }

    public List<Notification> getByStudentId(
            Long studentId,
            boolean unreadOnly) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        )
                );

        return unreadOnly
                ? notificationRepository.findUnreadByStudentId(studentId)
                : notificationRepository.findByStudentId(studentId);
    }

    public List<Notification> getByInstitutionId(
            Long institutionId,
            boolean unreadOnly) {

        institutionRepository.findById(institutionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );

        return unreadOnly
                ? notificationRepository.findUnreadByInstitutionId(institutionId)
                : notificationRepository.findByInstitutionId(institutionId);
    }

    public List<Notification> getForAdmin(boolean unreadOnly) {

        return unreadOnly
                ? notificationRepository.findUnreadForAdmin()
                : notificationRepository.findForAdmin();
    }

    public void markAsRead(Long notificationId) {

        notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: "
                                        + notificationId
                        )
                );

        notificationRepository.markAsRead(notificationId);
    }

    public int markAllAsRead(Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        )
                );

        return notificationRepository
                .markAllAsReadByStudentId(studentId);
    }

    public int markAllInstitutionNotificationsAsRead(
            Long institutionId) {

        institutionRepository.findById(institutionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );

        return notificationRepository
                .markAllAsReadByInstitutionId(institutionId);
    }

    public int markAllAdminNotificationsAsRead() {

        return notificationRepository.markAllAsReadForAdmin();
    }

    public int getUnreadCount(Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: " + studentId
                        )
                );

        return notificationRepository
                .countUnreadByStudentId(studentId);
    }

    public int getInstitutionUnreadCount(Long institutionId) {

        institutionRepository.findById(institutionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Institution not found with id: "
                                        + institutionId
                        )
                );

        return notificationRepository
                .countUnreadByInstitutionId(institutionId);
    }

    public int getAdminUnreadCount() {

        return notificationRepository.countUnreadForAdmin();
    }

    public void delete(Long notificationId) {

        notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id: "
                                        + notificationId
                        )
                );

        notificationRepository.deleteById(notificationId);
    }
}