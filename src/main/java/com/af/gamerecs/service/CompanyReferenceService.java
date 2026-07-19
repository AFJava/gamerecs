package com.af.gamerecs.service;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.CompanyReference;
import com.af.gamerecs.entities.FeatureType;
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

    public CompanyReference getCompanyReference(Long companyId, FeatureType companyRole) {
        return companyReferenceRepository.findByCompanyIgdbFeatureIdAndCompanyFeatureType(companyId, companyRole);
    }
    
    public Long getInvolvedCompanyId(Long companyId, FeatureType companyRole) {
        return getCompanyReference(companyId, companyRole).getInvolvedCompanyId();
    }
}
