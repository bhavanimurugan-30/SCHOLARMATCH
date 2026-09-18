package com.example.scholarmatch.institutioncontact.controller;

import com.example.scholarmatch.common.ApiResponse;
import com.example.scholarmatch.institutioncontact.dto.InstitutionContactPersonRequest;
import com.example.scholarmatch.institutioncontact.model.InstitutionContactPerson;
import com.example.scholarmatch.institutioncontact.service.InstitutionContactPersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions/{institutionId}/contacts")
public class InstitutionContactPersonController {

    private final InstitutionContactPersonService contactPersonService;

    public InstitutionContactPersonController(InstitutionContactPersonService contactPersonService) {
        this.contactPersonService = contactPersonService;
    }

    @PostMapping
    public ApiResponse<InstitutionContactPerson> create(@PathVariable Long institutionId,
                                                        @Valid @RequestBody InstitutionContactPersonRequest request) {
        return ApiResponse.success("Contact person added", contactPersonService.create(institutionId, request));
    }

    @GetMapping
    public ApiResponse<List<InstitutionContactPerson>> getAll(@PathVariable Long institutionId) {
        return ApiResponse.success(contactPersonService.getByInstitutionId(institutionId));
    }

    @PutMapping("/{contactId}")
    public ApiResponse<InstitutionContactPerson> update(@PathVariable Long institutionId,
                                                        @PathVariable Long contactId,
                                                        @Valid @RequestBody InstitutionContactPersonRequest request) {
        return ApiResponse.success("Contact person updated", contactPersonService.update(contactId, request));
    }

    @DeleteMapping("/{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long institutionId, @PathVariable Long contactId) {
        contactPersonService.delete(contactId);
    }
}