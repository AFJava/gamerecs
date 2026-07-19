package com.af.gamerecs.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.CompanyDto;
import com.af.gamerecs.dto.FeatureDto;
import com.af.gamerecs.dto.IgdbGameDto;
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
        LocalDate releaseDate = Instant.ofEpochSecond(game.first_release_date())
            .atZone(ZoneOffset.UTC)
            .toLocalDate();

        return new Game(game.id(),
                        game.name(),
                        game.cover().image_id(),
                        releaseDate,
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
            FeatureType companyType;

            if(company.developer()) {
                companyType = FeatureType.DEVELOPER;
            }
            else if(company.publisher()) {
                companyType = FeatureType.PUBLISHER;
            }
            else {
                //Generic involved company, weighted lower than developer/publisher
                companyType = FeatureType.INVOLVED_COMPANY;
            }

            features.add(new Feature(companyType, company.company().id(), company.company().name()));
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
}