package com.af.gamerecs.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.dto.IgdbGameDto;

@Service
public class IgdbService {
    private final TwitchProperties twitchProperties;
    private final TwitchAuthService twitchAuthService;
    private final WebClient webClient;

    public IgdbService(TwitchProperties twitchProperties, TwitchAuthService twitchAuthService, WebClient webClient) {
        this.twitchProperties = twitchProperties;
        this.twitchAuthService = twitchAuthService;
        this.webClient = webClient;
    }

    public List<IgdbGameDto> searchGames(String query) {
        String body = """
            fields id, name, cover, first_release_date, franchise.name, genres.name, game_modes.name, player_perspectives.name, platforms.name, keywords.name, tags.name, themes.name, rating, rating_count;
            search "%s";
            limit 20;
        """.formatted(query);

        IgdbGameDto[] games = webClient.post()
            .uri("/games")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(IgdbGameDto[].class)
            .block();
        
        return Arrays.asList(games);
    }
}
