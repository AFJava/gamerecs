package com.af.gamerecs.dto;

import java.util.List;

/* Class used to parse JSON response from RAWG API query  */
public record RawgSearchResponse(Integer count,
                                 String next,
                                 String previous,
                                 List<RawgGameDto> results) {

}
