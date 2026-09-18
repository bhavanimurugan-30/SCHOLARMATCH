package com.example.scholarmatch.financialinfo.service;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.financialinfo.dto.FinancialInfoRequest;
import com.example.scholarmatch.financialinfo.model.FinancialInfo;
import com.example.scholarmatch.financialinfo.repository.FinancialInfoRepository;
import com.example.scholarmatch.student.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class FinancialInfoService {

    private final FinancialInfoRepository financialInfoRepository;
    private final StudentRepository studentRepository;

    public FinancialInfoService(FinancialInfoRepository financialInfoRepository,
                                StudentRepository studentRepository) {
        this.financialInfoRepository = financialInfoRepository;
        this.studentRepository = studentRepository;
    }

    public FinancialInfo create(Long studentId, FinancialInfoRequest request) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        if (financialInfoRepository.existsByStudentId(studentId)) {
            throw new IllegalArgumentException("Financial info already exists for student id: " + studentId);
        }

        FinancialInfo info = new FinancialInfo();
        info.setStudentId(studentId);
        mapRequestToEntity(request, info);

        return financialInfoRepository.save(info);
    }

    public FinancialInfo getByStudentId(Long studentId) {
        return financialInfoRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Financial info not found for student id: " + studentId));
    }

    public FinancialInfo update(Long studentId, FinancialInfoRequest request) {
        FinancialInfo existing = getByStudentId(studentId);
        mapRequestToEntity(request, existing);
        financialInfoRepository.updateByStudentId(studentId, existing);
        return getByStudentId(studentId);
    }

    public void delete(Long studentId) {
        getByStudentId(studentId);
        financialInfoRepository.deleteByStudentId(studentId);
    }

    private void mapRequestToEntity(FinancialInfoRequest request, FinancialInfo info) {
        info.setAnnualFamilyIncome(request.getAnnualFamilyIncome());
        info.setFatherOccupation(request.getFatherOccupation());
        info.setMotherOccupation(request.getMotherOccupation());
        info.setBplStatus(request.getBplStatus());
        info.setBankAccountNumber(request.getBankAccountNumber());
        info.setBankIfsc(request.getBankIfsc());
        info.setBankName(request.getBankName());
    }
}