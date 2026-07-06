package com.af.gamerecs.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDto(Long id, 
                          String name,
                          ImageDto cover,
                          Long first_release_date,
                          NameDto franchise,
                          List<NameDto> franchises,
                          List<NameDto> involved_companies,
                          List<NameDto> platforms,
                          List<NameDto> genres,
                          List<NameDto> themes,
                          List<NameDto> game_modes,
                          List<NameDto> player_perspectives,
                          List<NameDto> keywords,
                          Double rating, //Rating from IGDB
                          Integer rating_count) {

}
