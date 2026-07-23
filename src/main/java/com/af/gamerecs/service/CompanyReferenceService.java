package com.af.gamerecs.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.CompanyDto;
import com.af.gamerecs.entities.CompanyReference;
import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.CompanyReferenceRepository;

@Service
public class CompanyReferenceService {
    public final CompanyReferenceRepository companyReferenceRepository;
    public final GameService gameService;
    public final IgdbService igdbService;

    public CompanyReferenceService(CompanyReferenceRepository companyReferenceRepository, GameService gameService, IgdbService igdbService) {
        this.companyReferenceRepository = companyReferenceRepository;
        this.gameService = gameService;
        this.igdbService = igdbService;
    }

    public void saveAllCompanyReferences(List<UserPreference> preferences) {
        for(UserPreference preference : preferences) {
            if(preference.getFeature().getFeatureType().isCompany()) {
                //Check if already stored
                List<CompanyReference> references = getAllCompanyReferences(
                    preference.getFeature().getIgdbFeatureId(), 
                    preference.getFeature().getFeatureType()
                );

                if(references.isEmpty() || ChronoUnit.DAYS.between(references.get(0).getAdded(), LocalDate.now()) > 30) {
                    List<CompanyDto> involvedCompanies = igdbService.getInvolvedCompanyInstances(
                        preference.getFeature().getIgdbFeatureId()
                    );

                    for(CompanyDto dto : involvedCompanies) {
                        references.add(new CompanyReference(
                            dto.id(),
                            preference.getFeature().getIgdbFeatureId(),
                            gameService.getCompanyRole(dto)
                        ));
                    }

                    companyReferenceRepository.saveAll(references);
                }

                //Delay?
                try {
                    Thread.sleep(500); //Pause for 500ms to avoid hitting rate limit
                }
                catch(InterruptedException e) {

                }
            }
        }
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
