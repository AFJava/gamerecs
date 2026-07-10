package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.entities.Feature;
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
            fields id,
                name,
                cover.image_id,
                first_release_date, 
                franchise.name,
                franchises.name,
                involved_companies.company.name,
                genres.name,
                themes.name,
                game_modes.name,
                player_perspectives.name,
                platforms.name,
                keywords.name,
                rating,
                rating_count;
            search "%s";
            limit 20;
        """.formatted(query);

        //System.out.println("Before POST");

        IgdbGameDto[] games = igdbWebClient.post()
            .uri("/games")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(IgdbGameDto[].class)
            .block();

        //System.out.println(twitchProperties.client_id());
        //System.out.println(twitchAuthService.getAccessToken());
        //System.out.println(Arrays.asList(games));
        
        return Arrays.asList(games);
    }

    // Assume topFeatures is not null and of set size
    public List<IgdbGameDto> searchMatchingGames(List<Feature> topFeatures) {
        String params = "";

        //Handle first case separately
        params += String.format("%s = (%d)", topFeatures.get(0).getFeatureType(), topFeatures.get(0).getIgdbFeatureId());
        
        for(int i = 0; i < topFeatures.size() - 1; i++) {
            Feature feature = topFeatures.get(i);
            params += String.format(" | %s = (%d)", feature.getFeatureType(), feature.getIgdbFeatureId());
        }
        
        System.out.println(params);

        String body = """
            fields id,
                name,
                cover.image_id,
                first_release_date, 
                franchise.name,
                franchises.name,
                involved_companies.company.name,
                genres.name,
                themes.name,
                game_modes.name,
                player_perspectives.name,
                platforms.name,
                keywords.name,
                rating,
                rating_count;
            where %s;
            limit 30;
        """.formatted(params);
        //System.out.println("Before POST");

        IgdbGameDto[] games = igdbWebClient.post()
            .uri("/games")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(IgdbGameDto[].class)
            .block();

        //System.out.println(twitchProperties.client_id());
        //System.out.println(twitchAuthService.getAccessToken());
        //System.out.println(Arrays.asList(games));
        
        return Arrays.asList(games);
    }
}
