package com.example.scholarmatch.bookmark.service;

import com.example.scholarmatch.bookmark.model.Bookmark;
import com.example.scholarmatch.bookmark.repository.BookmarkRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.notification.service.NotificationService;
import com.example.scholarmatch.scholarship.model.Scholarship;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.student.model.Student;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StudentRepository studentRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final NotificationService notificationService;

    public BookmarkService(
            BookmarkRepository bookmarkRepository,
            StudentRepository studentRepository,
            ScholarshipRepository scholarshipRepository,
            NotificationService notificationService) {

        this.bookmarkRepository = bookmarkRepository;
        this.studentRepository = studentRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.notificationService = notificationService;
    }

    /**
     * Adds a scholarship to Saved Scholarships.
     *
     * Saved and Applied are independent.
     *
     * If the bookmark already exists:
     * - saved_at is restored by repository.save()
     * - applied_at is NOT changed
     * - existing application remains intact
     */
    public Bookmark add(
            Long studentId,
            Long scholarshipId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + studentId
                        )
                );

        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scholarship not found with id: "
                                        + scholarshipId
                        )
                );

        Bookmark bookmark = new Bookmark();

        bookmark.setStudentId(studentId);
        bookmark.setScholarshipId(scholarshipId);

        /*
         * Repository.save() already handles:
         *
         * 1. New bookmark -> INSERT
         * 2. Existing bookmark -> UPDATE saved_at
         *
         * If applied_at already exists, repository preserves it.
         */
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Get all bookmark records for a student.
     */
    public List<Bookmark> getByStudentId(
            Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + studentId
                        )
                );

        return bookmarkRepository.findByStudentId(studentId);
    }

    /**
     * Get only saved scholarships.
     */
    public List<Bookmark> getSavedByStudentId(
            Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + studentId
                        )
                );

        return bookmarkRepository.findSavedByStudentId(studentId);
    }

    /**
     * Get only applied scholarships.
     */
    public List<Bookmark> getAppliedByStudentId(
            Long studentId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + studentId
                        )
                );

        return bookmarkRepository.findAppliedByStudentId(studentId);
    }

    /**
     * Removes a scholarship from Saved Scholarships.
     *
     * If the student already applied:
     *
     *     SAVED + APPLIED
     *              ↓
     *            APPLIED
     *
     * The application remains.
     *
     * If the student has not applied:
     *
     *     SAVED
     *       ↓
     *    bookmark deleted
     */
    public void remove(
            Long studentId,
            Long scholarshipId) {

        studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found with id: "
                                        + studentId
                        )
                );

        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Scholarship not found with id: "
                                        + scholarshipId
                        )
                );

        /*
         * The repository method already handles both cases:
         *
         * - applied_at IS NOT NULL
         *      -> saved_at becomes NULL
         *      -> status becomes APPLIED
         *
         * - applied_at IS NULL
         *      -> bookmark row is deleted
         */
        bookmarkRepository.deleteByStudentIdAndScholarshipId(
                studentId,
                scholarshipId
        );
    }

    /**
     * Confirms that the student has applied externally.
     *
     * Applied date is immutable.
     *
     * If already applied, nothing happens.
     */
    public void markApplied(
            Long studentId,
            Long scholarshipId) {

        Student student =
                studentRepository.findById(studentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found with id: "
                                                + studentId
                                )
                        );

        Scholarship scholarship =
                scholarshipRepository.findById(scholarshipId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Scholarship not found with id: "
                                                + scholarshipId
                                )
                        );

        /*
         * Check whether application already exists.
         *
         * We use findAppliedBookmark()
         * instead of the non-existing isApplied().
         */
        Optional<Bookmark> appliedBookmark =
                bookmarkRepository.findAppliedBookmark(
                        studentId,
                        scholarshipId
                );

        if (appliedBookmark.isPresent()) {
            return;
        }

        /*
         * Mark application.
         *
         * Repository handles:
         *
         * - existing saved bookmark
         *      -> SAVED_AND_APPLIED
         *
         * - no bookmark
         *      -> APPLIED
         *
         * applied_at is set only once.
         */
        bookmarkRepository.markApplied(
                studentId,
                scholarshipId
        );

        /*
         * Notify the institution that posted the scholarship.
         */
        Long institutionId =
                scholarship.getProviderInstitutionId();

        if (institutionId == null) {
            return;
        }

        notificationService.notifyInstitution(
                institutionId,
                "SCHOLARSHIP_APPLICATION",
                scholarshipId,
                "New scholarship application",
                "One student has applied for "
                        + scholarship.getTitle()
                        + "."
        );
    }
}