package com.af.gamerecs.dto;

import com.af.gamerecs.entities.Game;

public record FavGameRequest(Long igdbId,
                             IgdbGameDto game) {

}
