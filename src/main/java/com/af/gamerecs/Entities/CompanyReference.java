package com.af.gamerecs.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name = "company_reference",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "involved_company_id",
            "company_id"
        })
    }
)
public class CompanyReference {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(unique = true)
    Long involvedCompanyId;

    Long companyId;

    FeatureType role;

    LocalDate added;

    public CompanyReference() {
        added = LocalDate.now();
    }

    public CompanyReference(Long involvedCompanyId, Long companyId, FeatureType role) {
        this.involvedCompanyId = involvedCompanyId;
        this.companyId = companyId;
        this.role = role;
        added = LocalDate.now();
    }

    public Long getInvolvedCompanyId() {
        return involvedCompanyId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public FeatureType getRole() {
        return role;
    }

    public LocalDate getAdded() {
        return added;
    }
}
