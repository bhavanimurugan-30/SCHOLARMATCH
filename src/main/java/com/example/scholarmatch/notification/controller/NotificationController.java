package com.example.scholarmatch.notification.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.notification.dto.NotificationRequest;
import com.example.scholarmatch.notification.model.Notification;
import com.example.scholarmatch.notification.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ApiResponse<Notification> create(@Valid @RequestBody NotificationRequest request) {
        return ApiResponse.success("Notification created", notificationService.create(request));
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<Notification>> getForStudent(
            @PathVariable Long studentId,
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly) {
        return ApiResponse.success(notificationService.getByStudentId(studentId, unreadOnly));
    }

    @GetMapping("/institution/{institutionId}")
    public ApiResponse<List<Notification>> getForInstitution(
            @PathVariable Long institutionId,
            @RequestParam(value = "unreadOnly", defaultValue = "false") boolean unreadOnly) {
        return ApiResponse.success(notificationService.getByInstitutionId(institutionId, unreadOnly));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("Notification marked as read", null);
    }

    @PatchMapping("/student/{studentId}/read-all")
    public ApiResponse<Integer> markAllAsRead(@PathVariable Long studentId) {
        int updated = notificationService.markAllAsRead(studentId);
        return ApiResponse.success("All notifications marked as read", updated);
    }

    @GetMapping("/student/{studentId}/unread-count")
    public ApiResponse<Integer> unreadCount(@PathVariable Long studentId) {
        return ApiResponse.success(notificationService.getUnreadCount(studentId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        notificationService.delete(id);
    }
}