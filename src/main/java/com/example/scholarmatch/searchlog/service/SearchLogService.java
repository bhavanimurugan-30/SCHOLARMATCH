package com.example.scholarmatch.searchlog.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.searchlog.dto.SearchLogRequest;
import com.example.scholarmatch.searchlog.model.SearchLog;
import com.example.scholarmatch.searchlog.repository.SearchLogRepository;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SearchLogService {

    private final SearchLogRepository searchLogRepository;
    private final StudentRepository studentRepository;

    public SearchLogService(SearchLogRepository searchLogRepository, StudentRepository studentRepository) {
        this.searchLogRepository = searchLogRepository;
        this.studentRepository = studentRepository;
    }

    public SearchLog record(SearchLogRequest request) {
        if (request.getStudentId() != null) {
            studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student not found with id: " + request.getStudentId()));
        }

        SearchLog log = new SearchLog();
        log.setStudentId(request.getStudentId());
        log.setSearchQuery(request.getSearchQuery());
        log.setFiltersApplied(request.getFiltersApplied());

        return searchLogRepository.save(log);
    }

    public List<SearchLog> getByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        return searchLogRepository.findByStudentId(studentId);
    }

    public List<Map<String, Object>> getMostSearchedQueries(int limit) {
        return searchLogRepository.findMostSearchedQueries(limit);
    }
    public List<SearchLog> getRecent(int limit) {
        return searchLogRepository.findRecent(limit);
    }
}