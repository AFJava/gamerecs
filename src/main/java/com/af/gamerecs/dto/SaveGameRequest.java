package com.af.gamerecs.dto;

public record SaveGameRequest(Long igdbId,
                              Double rating,
                              IgdbGameDto game) {

}
