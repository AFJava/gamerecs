package com.af.gamerecs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgeRating(Long id,
                        @JsonProperty("rating_category") AgeRatingCategory ageRatingCategory) {

}
