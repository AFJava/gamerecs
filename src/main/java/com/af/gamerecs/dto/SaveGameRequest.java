package com.af.gamerecs.dto;

public record SaveGameRequest(Long rawgId,
                              float rating,
                              String name,
                              String imageSrc) {

}
