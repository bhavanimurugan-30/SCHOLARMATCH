package com.example.scholarmatch.institutioncontact.service;

import com.example.scholarmatch.exception.ResourceNotFoundException;
import com.example.scholarmatch.institution.repository.InstitutionRepository;
import com.example.scholarmatch.institutioncontact.dto.InstitutionContactPersonRequest;
import com.example.scholarmatch.institutioncontact.model.InstitutionContactPerson;
import com.example.scholarmatch.institutioncontact.repository.InstitutionContactPersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstitutionContactPersonService {

    private final InstitutionContactPersonRepository contactPersonRepository;
    private final InstitutionRepository institutionRepository;

    public InstitutionContactPersonService(InstitutionContactPersonRepository contactPersonRepository,
                                           InstitutionRepository institutionRepository) {
        this.contactPersonRepository = contactPersonRepository;
        this.institutionRepository = institutionRepository;
    }

    public InstitutionContactPerson create(Long institutionId, InstitutionContactPersonRequest request) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));

        InstitutionContactPerson contact = new InstitutionContactPerson();
        contact.setInstitutionId(institutionId);
        mapRequestToEntity(request, contact);

        return contactPersonRepository.save(contact);
    }

    public InstitutionContactPerson getById(Long contactPersonId) {
        return contactPersonRepository.findById(contactPersonId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contact person not found with id: " + contactPersonId));
    }

    public List<InstitutionContactPerson> getByInstitutionId(Long institutionId) {
        institutionRepository.findById(institutionId)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + institutionId));
        return contactPersonRepository.findByInstitutionId(institutionId);
    }

    public InstitutionContactPerson update(Long contactPersonId, InstitutionContactPersonRequest request) {
        InstitutionContactPerson existing = getById(contactPersonId);
        mapRequestToEntity(request, existing);
        contactPersonRepository.update(contactPersonId, existing);
        return getById(contactPersonId);
    }

    public void delete(Long contactPersonId) {
        getById(contactPersonId);
        contactPersonRepository.deleteById(contactPersonId);
    }

    private void mapRequestToEntity(InstitutionContactPersonRequest request, InstitutionContactPerson contact) {
        contact.setFullName(request.getFullName());
        contact.setDesignation(request.getDesignation());
        contact.setPhone(request.getPhone());
        contact.setEmail(request.getEmail());
    }
}