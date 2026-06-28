package com.af.gamerecs.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.RawgProperties;
import com.af.gamerecs.dto.RawgSearchResponse;

@Service
public class RawgService {
    private final WebClient webClient;
    private final RawgProperties rawgProperties;

    public RawgService(RawgProperties rawgProperties) {
        this.rawgProperties = rawgProperties;
        this.webClient = WebClient.create(rawgProperties.baseUrl());
    }

    /* Called by GameApiController /search endpoint to dynamically retrieve first 5 results from searchbar */
    public RawgSearchResponse searchGames(String query) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/games")
                .queryParam("key", rawgProperties.key())
                .queryParam("search", query)
                .queryParam("page_size", 20)
                .queryParam("search_precise", true)
                .build())
            .retrieve()
            .bodyToMono(RawgSearchResponse.class)
            .block();
    }
}
