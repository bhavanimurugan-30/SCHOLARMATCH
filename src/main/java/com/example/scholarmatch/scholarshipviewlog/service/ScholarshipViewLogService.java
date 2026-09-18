package com.example.scholarmatch.scholarshipviewlog.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.scholarship.repository.ScholarshipRepository;
import com.example.scholarmatch.scholarshipviewlog.model.ScholarshipViewLog;
import com.example.scholarmatch.scholarshipviewlog.repository.ScholarshipViewLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ScholarshipViewLogService {

    private final ScholarshipViewLogRepository viewLogRepository;
    private final ScholarshipRepository scholarshipRepository;

    public ScholarshipViewLogService(ScholarshipViewLogRepository viewLogRepository,
                                     ScholarshipRepository scholarshipRepository) {
        this.viewLogRepository = viewLogRepository;
        this.scholarshipRepository = scholarshipRepository;
    }

    /**
     * Records a view. studentId may be null for anonymous/untracked traffic.
     */
    @Transactional
    public ScholarshipViewLog record(Long scholarshipId, Long studentId) {
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));

        // Keep the denormalized view_count in sync with the actual view log.
        int updated = scholarshipRepository.incrementViewCount(scholarshipId);
        if (updated == 0) {
            throw new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId);
        }

        ScholarshipViewLog log = new ScholarshipViewLog();
        log.setScholarshipId(scholarshipId);
        log.setStudentId(studentId);

        return viewLogRepository.save(log);
    }

    public List<ScholarshipViewLog> getByScholarshipId(Long scholarshipId) {
        scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Scholarship not found with id: " + scholarshipId));
        return viewLogRepository.findByScholarshipId(scholarshipId);
    }

    public List<Map<String, Object>> getMostViewed(int limit) {
        return viewLogRepository.findMostViewed(limit);
    }

    public List<ScholarshipViewLog> getRecent(int limit) {
        return viewLogRepository.findRecent(limit);
    }

    public List<ScholarshipViewLog> getByStudentId(Long studentId) {
        return viewLogRepository.findByStudentId(studentId);
    }
}