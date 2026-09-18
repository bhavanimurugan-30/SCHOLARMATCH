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

    public NotificationService(NotificationRepository notificationRepository,
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

        if ("STUDENT".equals(request.getRecipientType())) {
            studentRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student not found with id: " + request.getRecipientId()));
            n.setStudentId(request.getRecipientId());
        } else {
            institutionRepository.findById(request.getRecipientId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Institution not found with id: " + request.getRecipientId()));
            n.setInstitutionId(request.getRecipientId());
        }

        if (request.getRelatedScholarshipId() != null) {
            scholarshipRepository.findById(request.getRelatedScholarshipId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Scholarship not found with id: " + request.getRelatedScholarshipId()));
            n.setRelatedScholarshipId(request.getRelatedScholarshipId());
        }

        return notificationRepository.save(n);
    }

    /**
     * Convenience method for internal use (e.g. by the scholarship-approval flow)
     * to fire a SCHOLARSHIP_APPROVED / SCHOLARSHIP_REJECTED notification without
     * building a full NotificationRequest.
     */
    public Notification notifyInstitution(Long institutionId, String type, Long scholarshipId,
                                          String title, String message) {
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

    /**
     * Convenience method for deadline/certificate-expiry alerts to a student
     * (Section 4.4), to be called by a future scheduled job.
     */
    public Notification notifyStudent(Long studentId, String type, Long scholarshipId,
                                      String title, String message) {
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

    public List<Notification> getByStudentId(Long studentId, boolean unreadOnly) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return unreadOnly
                ? notificationRepository.findUnreadByStudentId(studentId)
                : notificationRepository.findByStudentId(studentId);
    }

    public List<Notification> getByInstitutionId(Long institutionId, boolean unreadOnly) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));
        return unreadOnly
                ? notificationRepository.findUnreadByInstitutionId(institutionId)
                : notificationRepository.findByInstitutionId(institutionId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notificationRepository.markAsRead(notificationId);
    }

    /** Marks all of a student's unread notifications as read. Returns how many were updated. */
    public int markAllAsRead(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return notificationRepository.markAllAsReadByStudentId(studentId);
    }

    public int getUnreadCount(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return notificationRepository.countUnreadByStudentId(studentId);
    }

    public void delete(Long notificationId) {
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notificationRepository.deleteById(notificationId);
    }
}