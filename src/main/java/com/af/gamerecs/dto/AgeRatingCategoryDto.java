package com.af.gamerecs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgeRatingCategoryDto(Long id,
                                String rating,
                                @JsonProperty("organization") AgeRatingOrganizationDto ageRatingOrganization) {

}
