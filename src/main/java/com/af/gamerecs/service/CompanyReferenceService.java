package com.af.gamerecs.service;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.CompanyReference;
import com.af.gamerecs.repositories.CompanyReferenceRepository;

@Service
public class CompanyReferenceService {
    public final CompanyReferenceRepository companyReferenceRepository;

    public CompanyReferenceService(CompanyReferenceRepository companyReferenceRepository) {
        this.companyReferenceRepository = companyReferenceRepository;
    }

    public CompanyReference saveCompanyReference(CompanyReference reference) {
        return companyReferenceRepository.save(reference);
    }

    public CompanyReference getCompanyReferenceByCompanyId(Long companyId) {
        return companyReferenceRepository.findByCompanyId(companyId);
    }
    
    public Long getInvolvedCompanyId(Long companyId) {
        return companyReferenceRepository.findByCompanyId(companyId).getInvolvedCompanyId();
    }
}
