package com.af.gamerecs.dto;

public record SaveGameRequest(Long igdbId,
                              Integer rating,
                              IgdbGameDto game) {

}
