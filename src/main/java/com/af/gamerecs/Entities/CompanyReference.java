package com.af.gamerecs.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_reference")
public class CompanyReference {
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
