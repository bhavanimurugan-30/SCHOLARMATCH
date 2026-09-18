
        package com.example.scholarmatch.reminder.service;

import com.example.scholarmatch.bookmark.model.Bookmark;
import com.example.scholarmatch.bookmark.repository.BookmarkRepository;
import com.example.scholarmatch.notification.model.Notification;
import com.example.scholarmatch.notification.repository.NotificationRepository;
import com.example.scholarmatch.notification.service.NotificationService;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
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

    private static final String NOTIFICATION_TYPE =
            "DEADLINE_ALERT";

    private final ScholarshipRepository scholarshipRepository;
    private final BookmarkRepository bookmarkRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final JavaMailSender mailSender;

    @Value("${scholarship.reminder.days-before-deadline:7}")
    private int daysBeforeDeadline;

    public DeadlineReminderService(
            ScholarshipRepository scholarshipRepository,
            BookmarkRepository bookmarkRepository,
            StudentRepository studentRepository,
            NotificationRepository notificationRepository,
            NotificationService notificationService,
            JavaMailSender mailSender) {

        this.scholarshipRepository =
                scholarshipRepository;

        this.bookmarkRepository =
                bookmarkRepository;

        this.studentRepository =
                studentRepository;

        this.notificationRepository =
                notificationRepository;

        this.notificationService =
                notificationService;

        this.mailSender =
                mailSender;
    }

    /**
     * Runs daily at 08:00 server time.
     *
     * Only students who have SAVED the scholarship
     * receive the deadline reminder.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDeadlineReminders() {

        List<Scholarship> upcoming =
                scholarshipRepository
                        .findActiveApprovedWithDeadlineWithinDays(
                                daysBeforeDeadline
                        );

        for (Scholarship scholarship : upcoming) {

            /*
             * Find ONLY students who saved this
             * particular scholarship.
             */
            List<Bookmark> savedBookmarks =
                    bookmarkRepository
                            .findSavedByScholarshipId(
                                    scholarship.getScholarshipId()
                            );

            for (Bookmark bookmark : savedBookmarks) {

                Long studentId =
                        bookmark.getStudentId();

                /*
                 * Prevent duplicate deadline notifications
                 * for the same student + scholarship.
                 */
                boolean alreadyNotified =
                        notificationRepository
                                .existsByStudentScholarshipAndType(
                                        studentId,
                                        scholarship.getScholarshipId(),
                                        NOTIFICATION_TYPE
                                );

                if (alreadyNotified) {
                    continue;
                }

                Optional<Student> studentOpt =
                        studentRepository.findById(
                                studentId
                        );

                if (studentOpt.isEmpty()) {
                    continue;
                }

                Student student =
                        studentOpt.get();

                String deadlineStr =
                        scholarship.getDeadline()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd MMM yyyy"
                                        )
                                );

                String title =
                        "Scholarship Deadline Reminder";

                String message =
                        "Scholarship Deadline Reminder: "
                                + scholarship.getTitle()
                                + " deadline is "
                                + deadlineStr
                                + ".";

                /*
                 * Create the in-app notification.
                 */
                Notification saved =
                        notificationService.notifyStudent(
                                studentId,
                                NOTIFICATION_TYPE,
                                scholarship.getScholarshipId(),
                                title,
                                message
                        );

                /*
                 * Email is optional. If the student has
                 * an email address, send the same reminder.
                 */
                if (student.getEmail() != null) {

                    boolean emailSent =
                            sendEmail(
                                    student.getEmail(),
                                    title,
                                    message
                            );

                    if (emailSent) {

                        notificationRepository
                                .markEmailSent(
                                        saved.getNotificationId()
                                );
                    }
                }
            }
        }
    }

    /*
     * Manual trigger for testing.
     *
     * This executes the same deadline reminder logic
     * immediately without waiting until 08:00.
     */
    public void runNow() {
        sendDeadlineReminders();
    }

    private String buildMessage(
            Student student,
            Scholarship scholarship) {

        String deadlineStr =
                scholarship.getDeadline()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd MMM yyyy"
                                )
                        );

        return "Hi "
                + student.getFullName()
                + ", the scholarship \""
                + scholarship.getTitle()
                + "\" you are eligible for closes on "
                + deadlineStr
                + ". Please apply via the official link before the deadline.";
    }

    private boolean sendEmail(
            String toEmail,
            String subject,
            String body) {

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

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

