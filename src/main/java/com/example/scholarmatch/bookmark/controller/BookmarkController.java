package com.example.scholarmatch.bookmark.controller;

import com.example.scholarmatch.bookmark.model.Bookmark;
import com.example.scholarmatch.bookmark.service.BookmarkService;
import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.security.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students/{studentId}/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    private void assertOwnership(Long studentId, CustomUserPrincipal principal) {
        if (principal != null && "STUDENT".equals(principal.getRole()) && !studentId.equals(principal.getId())) {
            throw new AccessDeniedException("You can only manage your own bookmarks");
        }
    }

    @PostMapping("/{scholarshipId}")
    public ApiResponse<Bookmark> add(@PathVariable Long studentId, @PathVariable Long scholarshipId,
                                     @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        return ApiResponse.success("Scholarship bookmarked", bookmarkService.add(studentId, scholarshipId));
    }

    @GetMapping
    public ApiResponse<List<Bookmark>> getAll(@PathVariable Long studentId,
                                              @RequestParam(value = "savedOnly", defaultValue = "false") boolean savedOnly,
                                              @RequestParam(value = "appliedOnly", defaultValue = "false") boolean appliedOnly,
                                              @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        if (savedOnly) {
            return ApiResponse.success(bookmarkService.getSavedByStudentId(studentId));
        }
        if (appliedOnly) {
            return ApiResponse.success(bookmarkService.getAppliedByStudentId(studentId));
        }
        return ApiResponse.success(bookmarkService.getByStudentId(studentId));
    }

    @DeleteMapping("/{scholarshipId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long studentId, @PathVariable Long scholarshipId,
                       @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        bookmarkService.remove(studentId, scholarshipId);
    }

    @PatchMapping("/{scholarshipId}/apply")
    public ApiResponse<Void> markApplied(@PathVariable Long studentId, @PathVariable Long scholarshipId,
                                         @AuthenticationPrincipal CustomUserPrincipal principal) {
        assertOwnership(studentId, principal);
        bookmarkService.markApplied(studentId, scholarshipId);
        return ApiResponse.success("Marked as applied", null);
    }
}