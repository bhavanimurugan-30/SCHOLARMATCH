package com.example.scholarmatch.academicinfo.service;

import com.example.scholarmatch.academicinfo.dto.AcademicInfoRequest;
import com.example.scholarmatch.academicinfo.model.AcademicInfo;
import com.example.scholarmatch.academicinfo.repository.AcademicInfoRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class AcademicInfoService {

    private final AcademicInfoRepository academicInfoRepository;
    private final StudentRepository studentRepository;

    public AcademicInfoService(AcademicInfoRepository academicInfoRepository,
                               StudentRepository studentRepository) {
        this.academicInfoRepository = academicInfoRepository;
        this.studentRepository = studentRepository;
    }

    public AcademicInfo create(Long studentId, AcademicInfoRequest request) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (academicInfoRepository.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("Academic info already exists for student id: " + studentId);
        }

        AcademicInfo info = new AcademicInfo();
        info.setStudentId(studentId);
        mapRequestToEntity(request, info);

        return academicInfoRepository.save(info);
    }

    public AcademicInfo getByStudentId(Long studentId) {
        return academicInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Academic info not found for student id: " + studentId));
    }

    public AcademicInfo update(Long studentId, AcademicInfoRequest request) {
        AcademicInfo existing = getByStudentId(studentId);
        mapRequestToEntity(request, existing);
        academicInfoRepository.updateByStudentId(studentId, existing);
        return getByStudentId(studentId);
    }

    public void delete(Long studentId) {
        getByStudentId(studentId);
        academicInfoRepository.deleteByStudentId(studentId);
    }

    private void mapRequestToEntity(AcademicInfoRequest request, AcademicInfo info) {
        info.setEducationLevel(request.getEducationLevel());
        info.setCourseName(request.getCourseName());
        info.setInstitutionName(request.getInstitutionName());
        info.setCurrentYearSemester(request.getCurrentYearSemester());
        info.setQualifyingExamPercentage(request.getQualifyingExamPercentage());
        info.setSpecialization(request.getSpecialization());
        info.setUniversityBoard(request.getUniversityBoard());
        info.setAdmissionYear(request.getAdmissionYear());
        info.setModeOfStudy(
                request.getModeOfStudy() == null || request.getModeOfStudy().isBlank()
                        ? null
                        : request.getModeOfStudy()
        );
        info.setRollNumber(request.getRollNumber());
        info.setTenthPercentage(request.getTenthPercentage());
        info.setTwelfthPercentage(request.getTwelfthPercentage());
        info.setCurrentYear(request.getCurrentYear());
        info.setCurrentSemester(request.getCurrentSemester());
    }
}