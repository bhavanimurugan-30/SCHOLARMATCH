package com.example.scholarmatch.studentdocument.service;

import com.example.scholarmatch.certificatetype.repository.CertificateTypeRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.student.repository.StudentRepository;
import com.example.scholarmatch.studentdocument.dto.StudentDocumentRequest;
import com.example.scholarmatch.studentdocument.model.StudentDocument;
import com.example.scholarmatch.studentdocument.repository.StudentDocumentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class StudentDocumentService {

    private final StudentDocumentRepository studentDocumentRepository;
    private final StudentRepository studentRepository;
    private final CertificateTypeRepository certificateTypeRepository;

    public StudentDocumentService(StudentDocumentRepository studentDocumentRepository,
                                  StudentRepository studentRepository,
                                  CertificateTypeRepository certificateTypeRepository) {
        this.studentDocumentRepository = studentDocumentRepository;
        this.studentRepository = studentRepository;
        this.certificateTypeRepository = certificateTypeRepository;
    }

    public StudentDocument upload(Long studentId, StudentDocumentRequest request) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        certificateTypeRepository.findById(request.getCertificateTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate type not found with id: " + request.getCertificateTypeId()));

        StudentDocument doc = new StudentDocument();
        doc.setStudentId(studentId);
        doc.setCertificateTypeId(request.getCertificateTypeId());
        doc.setFilePath(request.getFilePath());
        doc.setOriginalFileName(request.getOriginalFileName());
        doc.setContentType(request.getContentType());
        doc.setFileSizeBytes(request.getFileSizeBytes());
        doc.setIssueDate(request.getIssueDate());
        doc.setExpiryDate(request.getExpiryDate());
        doc.setStatus(computeStatus(request.getExpiryDate()));

        // Re-uploading a certificate type the student already holds REPLACES the old record
        // (including an expired one) instead of creating a duplicate.
        Optional<StudentDocument> existing = studentDocumentRepository
                .findByStudentIdAndCertificateTypeId(studentId, request.getCertificateTypeId());
        if (existing.isPresent()) {
            Long documentId = existing.get().getDocumentId();
            studentDocumentRepository.replaceDocument(documentId, doc);
            return getById(documentId);
        }

        return studentDocumentRepository.save(doc);
    }

    public StudentDocument getById(Long documentId) {
        return studentDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    public List<StudentDocument> getByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return studentDocumentRepository.findByStudentId(studentId);
    }

    public StudentDocument update(Long documentId, StudentDocumentRequest request) {
        StudentDocument existing = getById(documentId);
        existing.setFilePath(request.getFilePath());
        existing.setIssueDate(request.getIssueDate());
        existing.setExpiryDate(request.getExpiryDate());
        existing.setStatus(computeStatus(request.getExpiryDate()));

        studentDocumentRepository.update(documentId, existing);
        return getById(documentId);
    }

    public void delete(Long documentId) {
        getById(documentId);
        studentDocumentRepository.deleteById(documentId);
    }

    /**
     * Status logic per Section 4.4: Valid / Expiring Soon (within 30 days) / Expired.
     */
    private String computeStatus(LocalDate expiryDate) {
        if (expiryDate == null) {
            return "VALID";
        }
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return "EXPIRED";
        }
        if (!expiryDate.isAfter(today.plusDays(30))) {
            return "EXPIRING_SOON";
        }
        return "VALID";
    }
}