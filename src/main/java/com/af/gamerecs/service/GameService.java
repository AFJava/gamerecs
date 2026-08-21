package com.af.gamerecs.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.af.gamerecs.repositories.FeatureRepository;
import com.af.gamerecs.repositories.GameRepository;

@Service
public class GameService {
    public final GameRepository gameRepository;
    public final FeatureService featureService;
    
    public GameService(GameRepository gameRepository, FeatureService featureService) {
        this.gameRepository = gameRepository;
        this.featureService = featureService;
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

    /* Game repo methods */
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
    
    /* Make single game */
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

    public Set<Feature> parseFeatures(List<FeatureDto> franchises,
            List<CompanyDto> companies,
            List<FeatureDto> platforms,
            List<FeatureDto> genres,
            List<FeatureDto> themes,
            List<FeatureDto> gameModes,
            List<FeatureDto> playerPerspectives,
            List<FeatureDto> keywords) {
        Set<Feature> features = new HashSet<>();
        
        addFeaturesToSet(features, franchises, FeatureType.FRANCHISE);
        addFeaturesToSet(features, platforms, FeatureType.PLATFORM);
        addFeaturesToSet(features, genres, FeatureType.GENRE);
        addFeaturesToSet(features, themes, FeatureType.THEME);
        addFeaturesToSet(features, gameModes, FeatureType.GAME_MODE);
        addFeaturesToSet(features, playerPerspectives, FeatureType.PLAYER_PERSPECTIVE);
        addFeaturesToSet(features, keywords, FeatureType.KEYWORD);

        addCompaniesToSet(features, companies);

        featureService.saveAllFeatures(features);

        return features;
    }

    public void addFeaturesToSet(Set<Feature> features, List<FeatureDto> dtos, FeatureType featureType) {
        if(dtos == null) {
            return;
        }

        List<Feature> existingFeatures = featureService.getExistingFeatures(
            featureType,
            getIgdbFeatureIdsFromDtos(dtos)
        );

        for(Feature feature : existingFeatures) {
            System.out.print(feature);
        }
        System.out.println();

        Map<Long, Feature> existingFeaturesMap = existingFeatures.stream()
            .collect(
                Collectors.toMap(
                    existingFeature -> existingFeature.getIgdbFeatureId(),
                    existingFeature -> existingFeature
                )
            );
        
        for(FeatureDto feature : safeList(dtos)) {
            if(existingFeaturesMap.containsKey(feature.id())) {
                features.add(existingFeaturesMap.get(feature.id()));
            } else {
                features.add(new Feature(featureType, feature.id(), feature.name()));
            }
        }
    }

    public void addCompaniesToSet(Set<Feature> features, List<CompanyDto> dtos) {
        if(dtos == null) {
            return;
        }

        List<FeatureDto> developers = new ArrayList<>();
        List<FeatureDto> publishers = new ArrayList<>();
        List<FeatureDto> supporting = new ArrayList<>();
        List<FeatureDto> porting = new ArrayList<>();

        for(CompanyDto dto : dtos) {
            if(dto.developer()) {
                developers.add(dto.company());
            }
            else if(dto.publisher()) {
                publishers.add(dto.company());
            }
            else if(dto.supporting()) {
                supporting.add(dto.company());
            }
            else {
                porting.add(dto.company());
            }
        }

        addFeaturesToSet(features, developers, FeatureType.DEVELOPER);
        addFeaturesToSet(features, publishers, FeatureType.PUBLISHER);
        addFeaturesToSet(features, supporting, FeatureType.SUPPORTING);
        addFeaturesToSet(features, porting, FeatureType.PORTING);
    }
    
    /* Make games in bulk */
    public List<Game> gamesFromDtos(List<IgdbGameDto> dtos) {
        //First handle features
        EnumMap<FeatureType, Map<Long, Feature>> allFeatures = new EnumMap<>(FeatureType.class);
        
        parseFeaturesBulk(allFeatures, dtos);

        List<Game> games = new ArrayList<>();
        for(IgdbGameDto dto : dtos) {
            gameFromDto(allFeatures, dto);
        }

        return saveAllGames(games);
    }

    /* Save all new features and populate the allFeaturesMap */
    public void parseFeaturesBulk(EnumMap<FeatureType, Map<Long, Feature>> allFeatures, List<IgdbGameDto> games) {
        Set<Feature> newFeatures = new HashSet<>();

        List<List<FeatureDto>> gameFranchises = new ArrayList<>();
        for(IgdbGameDto game : games) {
            gameFranchises.add(franchiseNames(game));
        }

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            gameFranchises,
            FeatureType.FRANCHISE);

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::platforms)
                .toList(),
            FeatureType.PLATFORM);
        
        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::genres)
                .toList(),
            FeatureType.GENRE);

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::themes)
                .toList(),
            FeatureType.THEME);

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::game_modes)
                .toList(),
            FeatureType.GAME_MODE);

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::player_perspectives)
                .toList(),
            FeatureType.PLAYER_PERSPECTIVE);

        parseAllFeaturesByType(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::keywords)
                .toList(),
            FeatureType.KEYWORD);

        parseCompaniesBulk(allFeatures,
            newFeatures,
            games.stream()
                .map(IgdbGameDto::involved_companies)
                .toList());

        featureService.saveAllFeatures(newFeatures);
    }

    public void parseCompaniesBulk(EnumMap<FeatureType, Map<Long, Feature>> allFeatures,
                                Set<Feature> newFeatures,
                                List<List<CompanyDto>> gameCompanies) {

    //List<List<FeatureDto>> implementation creates many new Lists (that often stay empty)
    List<FeatureDto> developers = new ArrayList<>();
    List<FeatureDto> publishers = new ArrayList<>();
    List<FeatureDto> supporting = new ArrayList<>();
    List<FeatureDto> porting = new ArrayList<>();

    for (List<CompanyDto> companies : gameCompanies) {
        for (CompanyDto dto : safeList(companies)) {
            FeatureDto company = dto.company();

            if (dto.developer()) {
                developers.add(company);
            }

            if (dto.publisher()) {
                publishers.add(company);
            }

            if (dto.supporting()) {
                supporting.add(company);
            }

            if (dto.porting()) {
                porting.add(company);
            }
        }
    }

    parseAllFeaturesByType(
        allFeatures,
        newFeatures,
        developers,
        FeatureType.DEVELOPER
    );

    parseAllFeaturesByType(
        allFeatures,
        newFeatures,
        publishers,
        FeatureType.PUBLISHER
    );

    parseAllFeaturesByType(
        allFeatures,
        newFeatures,
        supporting,
        FeatureType.SUPPORTING
    );

    parseAllFeaturesByType(
        allFeatures,
        newFeatures,
        porting,
        FeatureType.PORTING
    );
}

    /* Find all new features of current type and populate the allFeatures map for that type */
    public void parseAllFeaturesByType(EnumMap<FeatureType, Map<Long, Feature>> allFeatures,
                                    Set<Feature> newFeatures,
                                    List<List<FeatureDto>> gameFeatures,
                                    FeatureType type) {
        //Get all existing features of the current type
        List<Feature> existingFeatures = featureService.getExistingFeatures(
            type,
            getAllUniqueIgdbFeatureIdsFromDtos(
                gameFeatures
            )
        );

        Map<Long, Feature> featureMap = existingFeatures.stream()
            .collect(
                Collectors.toMap(
                    existingFeature -> existingFeature.getIgdbFeatureId(),
                    existingFeature -> existingFeature
                )
            );

        //Find and create a single instance of each unique feature; add it to the newFeatures set and featureMap
        for(List<FeatureDto> featureList : safeList(gameFeatures)) {
            for(FeatureDto feature : featureList) {
                if(! featureMap.containsKey(feature.id())) {
                    Feature newFeature = new Feature(type, feature.id(), feature.name());

                    newFeatures.add(newFeature);
                    featureMap.put(feature.id(), newFeature);
                }
            }
        }

        //Populate the allFeatures map
        allFeatures.put(type, featureMap);
    }

    /* Overload to allow efficient company parsing in parseCompaniesBulk */
    public void parseAllFeaturesByType(EnumMap<FeatureType, Map<Long, Feature>> allFeatures,
                                    Set<Feature> newFeatures,
                                    Collection<FeatureDto> features,
                                    FeatureType type) {
        Set<Long> featureIds = features.stream()
            .map(FeatureDto::id)
            .collect(Collectors.toSet());

        List<Feature> existingFeatures =
            featureService.getExistingFeatures(type, featureIds);

        Map<Long, Feature> featureMap = existingFeatures.stream()
            .collect(Collectors.toMap(
                Feature::getIgdbFeatureId,
                Function.identity()
            ));

        for (FeatureDto feature : features) {
            if (!featureMap.containsKey(feature.id())) {
                Feature newFeature =
                    new Feature(type, feature.id(), feature.name());

                newFeatures.add(newFeature);
                featureMap.put(feature.id(), newFeature);
            }
        }

        allFeatures.put(type, featureMap);
    }

    /* Overloaded for bulk processing */ 
    public Game gameFromDto(EnumMap<FeatureType, Map<Long, Feature>> allFeatures, IgdbGameDto game) {
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

        return new Game(game.id(),
                        game.name(),
                        imageId,
                        releaseDate,
                        parseAgeRatings(game),
                        parseFeatures(allFeatures,
                            franchiseNames(game),
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

    public Set<Feature> parseFeatures(EnumMap<FeatureType, Map<Long, Feature>> allFeatures,
            List<FeatureDto> franchises,
            List<CompanyDto> companies,
            List<FeatureDto> platforms,
            List<FeatureDto> genres,
            List<FeatureDto> themes,
            List<FeatureDto> gameModes,
            List<FeatureDto> playerPerspectives,
            List<FeatureDto> keywords) {
        Set<Feature> features = new HashSet<>();
        
        addFeaturesToSet(allFeatures, features, franchises, FeatureType.FRANCHISE);
        addFeaturesToSet(allFeatures, features, platforms, FeatureType.PLATFORM);
        addFeaturesToSet(allFeatures, features, genres, FeatureType.GENRE);
        addFeaturesToSet(allFeatures, features, themes, FeatureType.THEME);
        addFeaturesToSet(allFeatures, features, gameModes, FeatureType.GAME_MODE);
        addFeaturesToSet(allFeatures, features, playerPerspectives, FeatureType.PLAYER_PERSPECTIVE);
        addFeaturesToSet(allFeatures, features, keywords, FeatureType.KEYWORD);

        addCompaniesToSet(features, companies);

        return features;
    }

    public void addFeaturesToSet(EnumMap<FeatureType, Map<Long, Feature>> allFeatures,
        Set<Feature> features,
        List<FeatureDto> dtos,
        FeatureType type) {
        
        if(dtos == null) {
            return;
        }

        Map<Long, Feature> featureMap = allFeatures.get(type);
        
        for(FeatureDto feature : safeList(dtos)) {
            features.add(featureMap.get(feature.id()));
        }
    }

    public void addCompaniesToSet(EnumMap<FeatureType, Map<Long, Feature>> allFeatures, 
                                Set<Feature> features,
                                List<CompanyDto> dtos) {
        if(dtos == null) {
            return;
        }

        List<FeatureDto> developers = new ArrayList<>();
        List<FeatureDto> publishers = new ArrayList<>();
        List<FeatureDto> supporting = new ArrayList<>();
        List<FeatureDto> porting = new ArrayList<>();

        for(CompanyDto dto : dtos) {
            if(dto.developer()) {
                developers.add(dto.company());
            }
            else if(dto.publisher()) {
                publishers.add(dto.company());
            }
            else if(dto.supporting()) {
                supporting.add(dto.company());
            }
            else {
                porting.add(dto.company());
            }
        }

        addFeaturesToSet(features, developers, FeatureType.DEVELOPER);
        addFeaturesToSet(features, publishers, FeatureType.PUBLISHER);
        addFeaturesToSet(features, supporting, FeatureType.SUPPORTING);
        addFeaturesToSet(features, porting, FeatureType.PORTING);
    }

    /* Utility */
    public List<Long> getIgdbFeatureIdsFromDtos(List<FeatureDto> dtos) {
        return dtos.stream()
            .map(FeatureDto::id)
            .toList();
    }

    public List<Long> getIgdbIdsFromDtos(List<IgdbGameDto> dtos) {
        return dtos.stream()
            .map(IgdbGameDto::id)
            .toList();
    }

    /* Pass a list containing each games' list of features of a certain type */
    public Set<Long> getAllUniqueIgdbFeatureIdsFromDtos(List<List<FeatureDto>> gameFeatures) {
        Set<Long> idSet = new HashSet<>();

        for(List<FeatureDto> dtoList : gameFeatures) {
            for(FeatureDto dto : dtoList) {
                idSet.add(dto.id());
            }
        }

        return idSet;
    }
    
    private <T> List<T> safeList(List<T> list) {
        if(list == null) {
            return new ArrayList<>();
        }

        return list;
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
}