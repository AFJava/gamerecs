package com.af.gamerecs.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgdbGameDto(Long id, 
                          String name,
                          ImageDto cover,
                          Long first_release_date,
                          List<NameDto> franchise,
                          List<NameDto> genres,
                          List<NameDto> game_modes,
                          List<NameDto> player_perspectives,
                          List<NameDto> platforms,
                          List<NameDto> keywords,
                          Double rating, //Rating from IGDB
                          Integer rating_count) {

}
