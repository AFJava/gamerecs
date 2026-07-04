package com.af.gamerecs.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.dto.IgdbGameDto;

@Service
public class IgdbService {
    private final TwitchProperties twitchProperties;
    private final TwitchAuthService twitchAuthService;
    @Qualifier("igdbWebClient") private final WebClient igdbWebClient;

    public IgdbService(TwitchProperties twitchProperties, TwitchAuthService twitchAuthService, WebClient igdbWebClient) {
        this.twitchProperties = twitchProperties;
        this.twitchAuthService = twitchAuthService;
        this.igdbWebClient = igdbWebClient;
    }

    public List<IgdbGameDto> searchGames(String query) {
        String body = """
            fields id, name, cover.image_id, first_release_date, franchise.name, franchises.name, genres.name, themes.name, game_modes.name, player_perspectives.name, platforms.name, keywords.name, themes.name, rating, rating_count;
            search "%s";
            limit 20;
        """.formatted(query);

        IgdbGameDto[] games = igdbWebClient.post()
            .uri("/games")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(IgdbGameDto[].class)
            .block();
        
        //System.out.println(Arrays.asList(games));
        
        return Arrays.asList(games);
    }
}
