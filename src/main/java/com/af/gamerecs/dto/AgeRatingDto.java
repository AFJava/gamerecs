package com.af.gamerecs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgeRatingDto(Long id,
                        @JsonProperty("rating_category") AgeRatingCategoryDto ageRatingCategory) {

}
