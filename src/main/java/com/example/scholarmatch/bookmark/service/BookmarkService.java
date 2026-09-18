package com.example.scholarmatch.bookmark.service;

import com.example.scholarmatch.bookmark.model.Bookmark;
import com.example.scholarmatch.bookmark.repository.BookmarkRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final StudentRepository studentRepository;
    private final ScholarshipRepository scholarshipRepository;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                           StudentRepository studentRepository,
                           ScholarshipRepository scholarshipRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.studentRepository = studentRepository;
        this.scholarshipRepository = scholarshipRepository;
    }

    public Bookmark add(Long studentId, Long scholarshipId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        if (bookmarkRepository.isSaved(studentId, scholarshipId)) {
            throw new IllegalArgumentException("This scholarship is already saved");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setStudentId(studentId);
        bookmark.setScholarshipId(scholarshipId);

        return bookmarkRepository.save(bookmark);
    }

    public List<Bookmark> getByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return bookmarkRepository.findByStudentId(studentId);
    }

    public List<Bookmark> getSavedByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return bookmarkRepository.findSavedByStudentId(studentId);
    }

    public List<Bookmark> getAppliedByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return bookmarkRepository.findAppliedByStudentId(studentId);
    }

    public void remove(Long studentId, Long scholarshipId) {
        if (!bookmarkRepository.exists(studentId, scholarshipId)) {
            throw new ResourceNotFoundException("Bookmark not found for this student and scholarship");
        }
        bookmarkRepository.deleteByStudentIdAndScholarshipId(studentId, scholarshipId);
    }

    public void markApplied(Long studentId, Long scholarshipId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));
        bookmarkRepository.markApplied(studentId, scholarshipId);
    }
}