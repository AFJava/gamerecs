package com.af.gamerecs.dto;

public record SaveGameRequest(Long rawgId,
                              Integer rating,
                              RawgGameDto game) {

}
