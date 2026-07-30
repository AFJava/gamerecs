package com.af.gamerecs.dto;

import java.util.List;
import java.util.Set;

public record SearchResponse(List<IgdbGameDto> games, Set<Long> addedGamesIgdbIds, Set<Long> favoritedGamesIgdbIds) {

}
