package com.af.gamerecs.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.dto.CountResponse;
import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.Feature;

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

    //For dynamic searchbar
    public List<IgdbGameDto> searchGames(String query, boolean filterObscure) {
        //If 26, frontend adds button navigating to expanded search results (25 displayed, paginated)
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
            """;
        
        String where;
        
        if(! filterObscure) {
            where = """
                where name ~ *"%s"*;
                sort name asc;
                limit 26;
            """.formatted(query);
        }
        else {
            where = """
                where name ~ *"%s"* &
                rating != null &
                involved_companies != null;
                sort name asc;
                limit 26;
            """.formatted(query);
        }

        body += where;

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

    //For expanded search results
    public List<IgdbGameDto> searchGames(String query, int pageSize, int page, boolean filterObscure) {
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
            """;

        String where;
        
        if(! filterObscure) {
            where = """
                where name ~ *"%s"*;
                sort name asc;
                limit %d;
                offset %d;
            """.formatted(query, pageSize, (page - 1) * pageSize);
        }
        else {
            where = """
                where name ~ *"%s"* &
                rating != null &
                involved_companies != null;
                sort name asc;
                limit %d;
                offset %d;
            """.formatted(query, pageSize, (page - 1) * pageSize);
        }

        body += where;

        IgdbGameDto[] games = igdbWebClient.post()
            .uri("/games")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(IgdbGameDto[].class)
            .block();

        System.out.println(games.length);
        //System.out.println(Arrays.asList(games));
        
        return Arrays.asList(games);
    }

    public int numPages(String query, int pageSize, boolean filterObscure) {
        int numResults = numResults(query, filterObscure);

        return (int) Math.ceil((double) numResults / pageSize);
    }

    public int numResults(String query, boolean filterObscure) {
        String body = """
            where name ~ *"%s"*
        """.formatted(query);

        String where = ";";

        if(filterObscure) {
            where = """
                &
                rating != null &
                involved_companies != null;
            """;
        }

        body += where;

        CountResponse response = igdbWebClient.post()
            .uri("/games/count")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(CountResponse.class)
            .block();
        
        System.out.println(response.count());
        
        return response.count();
    }

    //Search for recommendations
    //Assume topFeatures is not null and of set size
    public List<IgdbGameDto> searchMatchingGames(List<Feature> topFeatures) {
        String params = "";

        //Handle first case separately
        //NOTE that IGDB field must be lowercase, plural (handled by FeatureType::toIgdbField())
        params += String.format("%s = (%d)", topFeatures.get(0).getFeatureType().toIgdbField(), topFeatures.get(0).getIgdbFeatureId());
        
        for(int i = 0; i < topFeatures.size() - 1; i++) {
            Feature feature = topFeatures.get(i);
            params += String.format(" | %s = (%d)", feature.getFeatureType().toIgdbField(), feature.getIgdbFeatureId());
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
