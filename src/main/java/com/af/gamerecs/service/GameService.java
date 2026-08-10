package com.af.gamerecs.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.AgeRatingDto;
import com.af.gamerecs.dto.CompanyDto;
import com.af.gamerecs.dto.FeatureDto;
import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.AgeRating;
import com.af.gamerecs.entities.AgeRatingOrganization;
import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.repositories.GameRepository;

@Service
public class GameService {
    public final GameRepository gameRepository;
    
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }
    
    /*
    public Game gameFromDto(RawgGameDto game) {
        return new Game(game.id(),
                        game.name(),
                        game.backgroundImage(),
                        game.released(),
                        game.metacritic());
    }
    */

    public Game gameFromDto(IgdbGameDto game) {
        LocalDate releaseDate = null;
        
        if (game.first_release_date() != null) {
            releaseDate = Instant.ofEpochSecond(game.first_release_date())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        }

        String imageId = null;

        if(game.cover() != null) {
            imageId = game.cover().image_id();
        }

        System.out.println(parseAgeRatings(game).getOrganization() + ": " + parseAgeRatings(game).getRating());

        return new Game(game.id(),
                        game.name(),
                        imageId,
                        releaseDate,
                        parseAgeRatings(game),
                        parseFeatures(franchiseNames(game),
                            game.involved_companies(),
                            game.platforms(),
                            game.genres(),
                            game.themes(),
                            game.game_modes(),
                            game.player_perspectives(),
                            game.keywords()),
                        game.rating(),
                        game.rating_count());
    }

    public Optional<Game> getGame(Long igdbId) {
        return gameRepository.findByIgdbId(igdbId);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    //This method is only used in RecommendationService.parseRecommendations, where only new games are passed as arguments; no check needed
    public List<Game> saveAllGames(List<Game> games) {
        return gameRepository.saveAll(games);
    }

    public List<Game> getGamesFromIgdbIds(List<Long> igdbIds) {
        return gameRepository.findAllByIgdbIdIn(igdbIds);
    }

    private List<FeatureDto> franchiseNames(IgdbGameDto game) {
        List<FeatureDto> result = new ArrayList<>();

        if (game.franchise() != null) {
            result.add(game.franchise());
        }

        if (game.franchises() != null) {
            result.addAll(game.franchises());
        }

        return result;
    }

    private AgeRating parseAgeRatings(IgdbGameDto game) {
        if(game.age_ratings() == null) {
            return null;
        }

        //Map from org ID to rating
        HashMap<String, String> orgMap = new HashMap<>();
        for(AgeRatingDto ratingDto : game.age_ratings()) {
            orgMap.put(ratingDto.ageRatingCategory().ageRatingOrganization().name(),
                        ratingDto.ageRatingCategory().rating());
        }

        //System.out.println("Parsing age rating...");
    
        //If ESRB rating exists, use it; if only PEGI exists, use that instead; if neither exist, keep null
        //ESRB -> 1, PEGI -> 2
        if(orgMap.containsKey("ESRB")) {
            return new AgeRating(AgeRatingOrganization.ESRB, orgMap.get("ESRB"));
        } else if(orgMap.containsKey("PEGI")) {
            return new AgeRating(AgeRatingOrganization.PEGI, orgMap.get("PEGI"));
        }

        return null;
    }
    
    private <T> List<T> safeList(List<T> list) {
        if(list == null) {
            return new ArrayList<>();
        }

        return list;
    }

    public Set<Feature> parseFeatures(List<FeatureDto> franchises,
            List<CompanyDto> companies,
            List<FeatureDto> platforms,
            List<FeatureDto> genres,
            List<FeatureDto> themes,
            List<FeatureDto> gameModes,
            List<FeatureDto> playerPerspectives,
            List<FeatureDto> keywords) {
        Set<Feature> features = new HashSet<>();
        
        for(FeatureDto franchise : safeList(franchises)) {
            features.add(new Feature(FeatureType.FRANCHISE, franchise.id(), franchise.name()));
        }

        for(CompanyDto company : safeList(companies)) {
            features.add(new Feature(getCompanyRole(company), company.company().id(), company.company().name()));
        }

        for(FeatureDto platform : safeList(platforms)) {
            features.add(new Feature(FeatureType.PLATFORM, platform.id(), platform.name()));
        }

        for(FeatureDto genre : safeList(genres)) {
            features.add(new Feature(FeatureType.GENRE, genre.id(), genre.name()));
        }

        for(FeatureDto theme : safeList(themes)) {
            features.add(new Feature(FeatureType.THEME, theme.id(), theme.name()));
        }

        for(FeatureDto gameMode : safeList(gameModes)) {
            features.add(new Feature(FeatureType.GAME_MODE, gameMode.id(), gameMode.name()));
        }

        for(FeatureDto playerPerspective : safeList(playerPerspectives)) {
            features.add(new Feature(FeatureType.PLAYER_PERSPECTIVE, playerPerspective.id(), playerPerspective.name()));
        }

        for(FeatureDto keyword : safeList(keywords)) {
            features.add(new Feature(FeatureType.KEYWORD, keyword.id(), keyword.name()));
        }

        return features;
    }

    public FeatureType getCompanyRole(CompanyDto company) {
        if(company.developer()) {
            return FeatureType.DEVELOPER;
        }
        else if(company.publisher()) {
            return FeatureType.PUBLISHER;
        }
        else if(company.porting()) {
            return FeatureType.PORTING;
        }
        else {
            return FeatureType.SUPPORTING;
        }
    }

    public List<Long> getIgdbIdsFromDtos(List<IgdbGameDto> dtos) {
        return dtos.stream()
            .map(IgdbGameDto::id)
            .toList();
    }
}