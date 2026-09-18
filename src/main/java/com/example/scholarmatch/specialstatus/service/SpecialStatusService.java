package com.example.scholarmatch.specialstatus.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.specialstatus.dto.SpecialStatusRequest;
import com.example.scholarmatch.specialstatus.model.SpecialStatus;
import com.example.scholarmatch.specialstatus.repository.SpecialStatusRepository;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class SpecialStatusService {

    private final SpecialStatusRepository specialStatusRepository;
    private final StudentRepository studentRepository;

    public SpecialStatusService(SpecialStatusRepository specialStatusRepository,
                                StudentRepository studentRepository) {
        this.specialStatusRepository = specialStatusRepository;
        this.studentRepository = studentRepository;
    }

    public SpecialStatus create(Long studentId, SpecialStatusRequest request) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (specialStatusRepository.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("Special status already exists for student id: " + studentId);
        }

        SpecialStatus status = new SpecialStatus();
        status.setStudentId(studentId);
        mapRequestToEntity(request, status);

        return specialStatusRepository.save(status);
    }

    public SpecialStatus getByStudentId(Long studentId) {
        return specialStatusRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Special status not found for student id: " + studentId));
    }

    public SpecialStatus update(Long studentId, SpecialStatusRequest request) {
        SpecialStatus existing = getByStudentId(studentId);
        mapRequestToEntity(request, existing);
        specialStatusRepository.updateByStudentId(studentId, existing);
        return getByStudentId(studentId);
    }

    public void delete(Long studentId) {
        getByStudentId(studentId);
        specialStatusRepository.deleteByStudentId(studentId);
    }

    private void mapRequestToEntity(SpecialStatusRequest request, SpecialStatus status) {
        status.setSingleGirlChild(request.isSingleGirlChild());
        status.setOrphanSingleParent(request.isOrphanSingleParent());
        status.setExServicemenDependent(request.isExServicemenDependent());
        status.setSportsQuota(request.isSportsQuota());
        status.setMinorityCommunity(request.isMinorityCommunity());
        status.setFirstGraduate(request.isFirstGraduate());
    }
}