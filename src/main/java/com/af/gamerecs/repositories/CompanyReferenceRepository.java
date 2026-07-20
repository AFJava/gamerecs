package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.CompanyReference;
import com.af.gamerecs.entities.FeatureType;

public interface CompanyReferenceRepository extends JpaRepository<CompanyReference, Integer>{
    public List<CompanyReference> findAllByCompanyIdAndRole(Long companyId, FeatureType role);
}
