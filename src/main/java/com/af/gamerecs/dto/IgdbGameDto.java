package com.af.gamerecs.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDto(Long id, 
                          String name,
                          ImageDto cover,
                          Long first_release_date,
                          List<AgeRatingDto> age_ratings,
                          FeatureDto franchise,
                          List<FeatureDto> franchises,
                          List<CompanyDto> involved_companies,
                          List<FeatureDto> platforms,
                          List<FeatureDto> genres,
                          List<FeatureDto> themes,
                          List<FeatureDto> game_modes,
                          List<FeatureDto> player_perspectives,
                          List<FeatureDto> keywords,
                          Double rating, //Rating from IGDB
                          Integer rating_count) {

}
