package com.af.gamerecs.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AgeRating {
    @Enumerated(EnumType.STRING)
    private AgeRatingOrganization organization;

    private String rating;

    public AgeRating() {

    }

    public AgeRating(AgeRatingOrganization organization, String rating) {
        this.organization = organization;
        this.rating = rating;
    }

    public AgeRatingOrganization getOrganization() {
        return organization;
    }

    public String getRating() {
        return rating;
    }
}
