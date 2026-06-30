package com.af.gamerecs.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawgGameDto(@JsonProperty("rawgId") Integer id, 
                          String name,
                          @JsonProperty("background_image") String backgroundImage,
                          Integer rating,
                          LocalDate released,
                          Integer metacritic,
                          Integer added,
                          Integer ratings_count) {

}
