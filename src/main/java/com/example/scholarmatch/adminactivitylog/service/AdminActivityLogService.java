package com.example.scholarmatch.adminactivitylog.service;

import com.example.scholarmatch.adminactivitylog.dto.AdminActivityLogRequest;
import com.example.scholarmatch.adminactivitylog.model.AdminActivityLog;
import com.example.scholarmatch.adminactivitylog.repository.AdminActivityLogRepository;
import com.example.scholarmatch.admin.repository.AdminRepository;
import com.example.scholarmatch.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminActivityLogService {

    private final AdminActivityLogRepository activityLogRepository;
    private final AdminRepository adminRepository;

    public AdminActivityLogService(AdminActivityLogRepository activityLogRepository,
                                   AdminRepository adminRepository) {
        this.activityLogRepository = activityLogRepository;
        this.adminRepository = adminRepository;
    }

    public AdminActivityLog record(AdminActivityLogRequest request) {
        adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + request.getAdminId()));

        AdminActivityLog log = new AdminActivityLog();
        log.setAdminId(request.getAdminId());
        log.setActionType(request.getActionType());
        log.setTargetEntity(request.getTargetEntity());
        log.setTargetId(request.getTargetId());
        log.setDetails(request.getDetails());

        return activityLogRepository.save(log);
    }

    /**
     * Convenience overload for internal calls from other services (e.g. after a
     * scholarship approval/rejection) without needing to build a full request DTO.
     */
    public AdminActivityLog record(Long adminId, String actionType, String targetEntity,
                                   Long targetId, String details) {
        AdminActivityLog log = new AdminActivityLog();
        log.setAdminId(adminId);
        log.setActionType(actionType);
        log.setTargetEntity(targetEntity);
        log.setTargetId(targetId);
        log.setDetails(details);
        return activityLogRepository.save(log);
    }

    public List<AdminActivityLog> getByAdminId(Long adminId) {
        adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with id: " + adminId));
        return activityLogRepository.findByAdminId(adminId);
    }

    public List<AdminActivityLog> getByTarget(String targetEntity, Long targetId) {
        return activityLogRepository.findByTargetEntityAndTargetId(targetEntity, targetId);
    }

    public List<AdminActivityLog> getAll() {
        return activityLogRepository.findAll();
    }
}