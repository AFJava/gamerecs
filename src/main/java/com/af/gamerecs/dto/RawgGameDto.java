package com.af.gamerecs.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawgGameDto(Integer rawgId, 
                          String name,
                          @JsonProperty("background_image") String imageSrc,
                          Integer rating,
                          List<String> genres,
                          List<String> tags, 
                          List<String> developers,
                          List<String> publishers,
                          List<String> platforms,
                          LocalDate released,
                          Integer metacritic,
                          Integer added,
                          Integer ratings_count) {

}
