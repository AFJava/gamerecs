package com.af.gamerecs.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "company_reference")
public class CompanyReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    Long involvedCompanyId;

    @Column(unique = true)
    Long companyId;

    public CompanyReference() {

    }

    public CompanyReference(Long involvedCompanyId, Long companyId) {
        this.involvedCompanyId = involvedCompanyId;
        this.companyId = companyId;
    }

    public Long getInvolvedCompanyId() {
        return involvedCompanyId;
    }

    public Long getCompanyId() {
        return companyId;
    }
}
