package com.af.gamerecs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgeRatingCategory(Long id,
                                String rating,
                                @JsonProperty("organization") AgeRatingOrganization ageRatingOrganization) {

}
