package com.af.gamerecs.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.UniqueConstraint;


@Entity
@Table(name = "company_reference",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "involved_company_id",
            "feature_id"
        })
    }
)
public class CompanyReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    Long involvedCompanyId;

    @Embedded
    Feature company;

    public CompanyReference() {

    }

    public CompanyReference(Long involvedCompanyId, Feature company) {
        this.involvedCompanyId = involvedCompanyId;
        this.company = company;
    }

    public Long getInvolvedCompanyId() {
        return involvedCompanyId;
    }

    public Feature getCompanyId() {
        return company;
    }
}
