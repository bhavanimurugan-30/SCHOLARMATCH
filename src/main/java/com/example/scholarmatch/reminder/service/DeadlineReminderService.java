package com.example.scholarmatch.reminder.service;

import com.example.scholarmatch.notification.model.Notification;
import com.example.scholarmatch.notification.repository.NotificationRepository;
import com.example.scholarmatch.notification.service.NotificationService;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.scholarshipmatchscore.model.ScholarshipMatchScore;
import com.example.scholarmatch.scholarshipmatchscore.repository.ScholarshipMatchScoreRepository;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class DeadlineReminderService {

    private static final String NOTIFICATION_TYPE = "DEADLINE_ALERT";
    private static final double ELIGIBLE_MATCH_THRESHOLD = 50.0;

    private final ScholarshipRepository scholarshipRepository;
    private final ScholarshipMatchScoreRepository matchScoreRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final JavaMailSender mailSender;

    @Value("${scholarship.reminder.days-before-deadline:7}")
    private int daysBeforeDeadline;

    public DeadlineReminderService(ScholarshipRepository scholarshipRepository,
                                   ScholarshipMatchScoreRepository matchScoreRepository,
                                   StudentRepository studentRepository,
                                   NotificationRepository notificationRepository,
                                   NotificationService notificationService,
                                   JavaMailSender mailSender) {
        this.scholarshipRepository = scholarshipRepository;
        this.matchScoreRepository = matchScoreRepository;
        this.studentRepository = studentRepository;
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
        this.mailSender = mailSender;
    }

    /** Runs daily at 08:00 server time. */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDeadlineReminders() {
        List<Scholarship> upcoming = scholarshipRepository.findActiveApprovedWithDeadlineWithinDays(daysBeforeDeadline);

        for (Scholarship scholarship : upcoming) {
            List<ScholarshipMatchScore> matches = matchScoreRepository.findByScholarshipId(scholarship.getScholarshipId());

            for (ScholarshipMatchScore match : matches) {
                if (match.getMatchPercentage() == null || match.getMatchPercentage() < ELIGIBLE_MATCH_THRESHOLD) {
                    continue;
                }

                boolean alreadyNotified = notificationRepository.existsByStudentScholarshipAndType(
                        match.getStudentId(), scholarship.getScholarshipId(), NOTIFICATION_TYPE);

                if (alreadyNotified) {
                    continue;
                }

                Optional<Student> studentOpt = studentRepository.findById(match.getStudentId());
                if (studentOpt.isEmpty() || studentOpt.get().getEmail() == null) {
                    continue;
                }

                Student student = studentOpt.get();
                String title = "Deadline approaching: " + scholarship.getTitle();
                String message = buildMessage(student, scholarship);

                Notification saved = notificationService.notifyStudent(
                        student.getStudentId(), NOTIFICATION_TYPE, scholarship.getScholarshipId(), title, message);

                boolean sent = sendEmail(student.getEmail(), title, message);
                if (sent) {
                    notificationRepository.markEmailSent(saved.getNotificationId());
                }
            }
        }
    }

    private String buildMessage(Student student, Scholarship scholarship) {
        String deadlineStr = scholarship.getDeadline().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        return "Hi " + student.getFullName() + ", the scholarship \"" + scholarship.getTitle() +
                "\" you are eligible for closes on " + deadlineStr +
                ". Please apply via the official link before the deadline.";
    }

    private boolean sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}