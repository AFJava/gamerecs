package com.af.gamerecs.service;

import java.util.List;

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

    public List<CompanyReference> saveAllCompanyReferences(List<CompanyReference> references) {
        return companyReferenceRepository.saveAll(references);
    }

    public List<CompanyReference> getAllCompanyReferences(Long companyId, FeatureType role) {
        return companyReferenceRepository.findAllByCompanyIdAndRole(companyId, role);
    }
    public List<Long> getAllInvolvedCompanyIds(Long companyId, FeatureType role) {
        return companyReferenceRepository.findAllByCompanyIdAndRole(companyId, role).stream()
            .map(CompanyReference::getInvolvedCompanyId)
            .toList();
    }
}
