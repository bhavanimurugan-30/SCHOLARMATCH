package com.example.scholarmatch.notification.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.notification.dto.NotificationRequest;
import com.example.scholarmatch.notification.model.Notification;
import com.example.scholarmatch.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<Notification> create(
            @Valid @RequestBody NotificationRequest request) {

        return ApiResponse.success(
                "Notification created",
                notificationService.create(request)
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Notification>> getForStudent(
            @PathVariable Long studentId,
            @RequestParam(
                    value = "unreadOnly",
                    defaultValue = "false"
            ) boolean unreadOnly) {

        return ApiResponse.success(
                notificationService.getByStudentId(
                        studentId,
                        unreadOnly
                )
        );
    }

    @PreAuthorize("hasAnyRole('INSTITUTION', 'ADMIN')")
    @GetMapping("/institution/{institutionId}")
    public ApiResponse<List<Notification>> getForInstitution(
            @PathVariable Long institutionId,
            @RequestParam(
                    value = "unreadOnly",
                    defaultValue = "false"
            ) boolean unreadOnly) {

        return ApiResponse.success(
                notificationService.getByInstitutionId(
                        institutionId,
                        unreadOnly
                )
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ApiResponse<List<Notification>> getForAdmin(
            @RequestParam(
                    value = "unreadOnly",
                    defaultValue = "false"
            ) boolean unreadOnly) {

        return ApiResponse.success(
                notificationService.getForAdmin(unreadOnly)
        );
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(
            @PathVariable("id") Long id) {

        notificationService.markAsRead(id);

        return ApiResponse.success(
                "Notification marked as read",
                null
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @PatchMapping("/student/{studentId}/read-all")
    public ApiResponse<Integer> markAllAsRead(
            @PathVariable Long studentId) {

        int updated =
                notificationService.markAllAsRead(studentId);

        return ApiResponse.success(
                "All notifications marked as read",
                updated
        );
    }

    @PreAuthorize("hasAnyRole('INSTITUTION', 'ADMIN')")
    @PatchMapping("/institution/{institutionId}/read-all")
    public ApiResponse<Integer> markAllInstitutionAsRead(
            @PathVariable Long institutionId) {

        int updated =
                notificationService
                        .markAllInstitutionNotificationsAsRead(
                                institutionId
                        );

        return ApiResponse.success(
                "All notifications marked as read",
                updated
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/read-all")
    public ApiResponse<Integer> markAllAdminAsRead() {

        int updated =
                notificationService
                        .markAllAdminNotificationsAsRead();

        return ApiResponse.success(
                "All admin notifications marked as read",
                updated
        );
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/student/{studentId}/unread-count")
    public ApiResponse<Integer> unreadCount(
            @PathVariable Long studentId) {

        return ApiResponse.success(
                notificationService.getUnreadCount(studentId)
        );
    }

    @PreAuthorize("hasAnyRole('INSTITUTION', 'ADMIN')")
    @GetMapping("/institution/{institutionId}/unread-count")
    public ApiResponse<Integer> institutionUnreadCount(
            @PathVariable Long institutionId) {

        return ApiResponse.success(
                notificationService
                        .getInstitutionUnreadCount(institutionId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/unread-count")
    public ApiResponse<Integer> adminUnreadCount() {

        return ApiResponse.success(
                notificationService.getAdminUnreadCount()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id) {

        notificationService.delete(id);
    }
}