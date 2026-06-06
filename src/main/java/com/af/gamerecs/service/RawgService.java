package com.af.gamerecs.service;

import com.af.gamerecs.config.RawgProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RawgService {
    private final WebClient webClient;
    private final RawgProperties rawgProperties;

    public RawgService(RawgProperties rawgProperties) {
        this.rawgProperties = rawgProperties;
        this.webClient = WebClient.create(rawgProperties.baseUrl());
    }

    public String searchGames(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", rawgProperties.key())
                        .queryParam("search", query)
                        .queryParam("page_size", 5)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
