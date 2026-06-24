package com.af.gamerecs.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/* Class used to parse JSON data of individual games from RAWG API */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RawgGameDto(Integer id, 
                          String name,
                          @JsonProperty("background_image") String backgroundImage,
                          Double rating,
                          String released,
                          Integer added,
                          Integer ratings_count,
                          Integer metacritic) {

}
