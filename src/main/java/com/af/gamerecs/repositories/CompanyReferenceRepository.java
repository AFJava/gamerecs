package com.af.gamerecs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.CompanyReference;

public interface CompanyReferenceRepository extends JpaRepository<CompanyReference, Integer>{
    public CompanyReference findByCompanyId(Long companyId);
}
