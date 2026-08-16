package com.af.gamerecs.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.af.gamerecs.config.TwitchProperties;
import com.af.gamerecs.dto.CountResponse;
import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.dto.CompanyDto;

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
                age_ratings.rating_category.rating,
                age_ratings.rating_category.organization.name,
                franchise.name,
                franchises.name,
                involved_companies.company.name,
                involved_companies.developer,
                involved_companies.publisher,
                involved_companies.supporting,
                involved_companies.porting,
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
                age_ratings.rating_category.rating,
                age_ratings.rating_category.organization.name,
                franchise.name,
                franchises.name,
                involved_companies.company.name,
                involved_companies.developer,
                involved_companies.publisher,
                involved_companies.supporting,
                involved_companies.porting,
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
    public List<IgdbGameDto> searchMatchingGames(String params) {
        String body = """
            fields id,
                name,
                cover.image_id,
                first_release_date,
                age_ratings.rating_category.rating,
                age_ratings.rating_category.organization.name,
                franchise.name,
                franchises.name,
                involved_companies.company.name,
                involved_companies.developer,
                involved_companies.publisher,
                involved_companies.supporting,
                involved_companies.porting,
                genres.name,
                themes.name,
                game_modes.name,
                player_perspectives.name,
                platforms.name,
                keywords.name,
                rating,
                rating_count;
            limit 100;
            where %s;
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

        System.out.println("Search matching successful");
        //System.out.println(Arrays.asList(games));
        
        return Arrays.asList(games);
    }

    public int countMatchingGames(String params) {
        String body = """
            where %s
        """.formatted(params);

        CountResponse response = igdbWebClient.post()
            .uri("/games/count")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(CountResponse.class)
            .block();

        return response.count();
    }

    public List<CompanyDto> getInvolvedCompanyInstances(Long companyId) {
        String body = """
            fields id, developer, publisher, supporting, porting;
            where company = %d;
        """.formatted(companyId);

        CompanyDto[] response = igdbWebClient.post()
            .uri("/involved_companies")
            .header("Client-ID", twitchProperties.client_id())
            .header("Authorization", "Bearer " + twitchAuthService.getAccessToken())
            .bodyValue(body)
            .retrieve()
            .bodyToMono(CompanyDto[].class)
            .block();

        return Arrays.asList(response);
    }
}
