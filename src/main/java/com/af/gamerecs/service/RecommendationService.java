package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.Recommendation;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.RecommendationRepository;

@Service
public class RecommendationService {
    public final RecommendationRepository recommendationRepository;
    public final UserGameService userGameService;
    public final GameService gameService;

    public RecommendationService(RecommendationRepository recommendationRepository, UserGameService userGameService, GameService gameService) {
        this.recommendationRepository = recommendationRepository;
        this.gameService = gameService;
        this.userGameService = userGameService;
    }

    public List<Recommendation> getExistingRecommendations(Long userId, List<Long> igdbIds) {
        return recommendationRepository.findAllMatchingUserRecommendations(userId, igdbIds);
    }

    public List<Recommendation> getActiveRecommendations(Long userId) {
        return recommendationRepository.findAllActiveRecommendations(userId);
    }

    public Page<Recommendation> getPaginatedActiveRecommendations(Long userId, Pageable pageable) {
        return recommendationRepository.findAllActiveRecommendations(userId, pageable);
    }

    public Recommendation getRecommendation(Long userId, Long igdbId) {
        return recommendationRepository.findRecommendation(userId, igdbId);
    }

    public List<Long> getAddedIgdbIds(Long userId) {
        return recommendationRepository.findAddedIgdbIds(userId);
    }

    public List<Recommendation> sortRecommendations(User user, List<IgdbGameDto> gameDtos, List<UserPreference> preferences) {
        clearCurrentBatch(user.getId());
        
        List<Recommendation> recs = parseRecommendations(user, gameDtos);

        for(int i = 0; i < recs.size(); i++) {
            Recommendation rec = recs.get(i);
            rec.setScore(scoreRecommendation(rec, preferences));
        }

        recs.sort(Comparator.comparing(rec -> rec.getScore()));
        
        return recommendationRepository.saveAll(recs);
    }

    public List<Recommendation> parseRecommendations(User user, List<IgdbGameDto> gameDtos) {
        List<Long> igdbIds = gameService.getIgdbIdsFromDtos(gameDtos);
        List<Recommendation> recs = getExistingRecommendations(user.getId(), igdbIds);
        
        //Creation of new Recommendation objects is expensive; use existing if possible
        Map<Long, Recommendation> existingRecsMap = recs.stream()
            .collect(Collectors.toMap(
                existingRec -> existingRec.getGame().getIgdbId(),
                existingRec -> existingRec
            ));

        List<Game> existingGames = gameService.getGamesFromIgdbIds(igdbIds);

        //Creation of new Game objects is also expensive; also ensures no duplicate games are saved
        Map<Long, Game> existingGamesMap = existingGames.stream()
            .collect(Collectors.toMap(
                game -> game.getIgdbId(),
                game -> game
            ));
        
        HashSet<Long> addedIgdbIds = new HashSet<>(userGameService.getAddedIgdbIds(user.getId(), igdbIds));

        List<Game> newGames = new ArrayList<>();
        
        for(IgdbGameDto dto : gameDtos) {
            //If already added, remove from recommendation pool
            if(! addedIgdbIds.contains(dto.id())) {
                //If rec exists, the associated game must also exist already
                if(! existingRecsMap.containsKey(dto.id())) {
                    Game game;

                    if(existingGamesMap.containsKey(dto.id())) {
                        game = existingGamesMap.get(dto.id());

                        /* 
                        if(game != null) {
                            System.out.println("Existing game " + game.getName());
                        }
                        else {
                            System.out.println("Existing game is null");
                        }
                        */
                    }
                    else {
                        game = gameService.gameFromDto(dto);
                        newGames.add(game);

                        /* 
                        if(game != null) {
                            System.out.println("New game " + game.getName());
                        }
                        else {
                            System.out.println("New game is null");
                        }
                        */
                    }

                    recs.add(new Recommendation(
                        user,
                        game
                    ));
                }
            }
        }

        //Save any new games
        gameService.saveAllGames(newGames);

        return recs;
    }

    public double scoreRecommendation(Recommendation rec, List<UserPreference> preferences) {
        Game game = rec.getGame();
        Set<Feature> gameFeatures = game.getFeatures();
        
        double score = 0;

        for(UserPreference preference : preferences) {
            if(gameFeatures.contains(preference.getFeature())) {
                score += preference.getWeight();
            }
        }

        score -= rec.getPressure();

        return score;
    }

    public void clearCurrentBatch(Long userId) {
        List<Recommendation> currentBatch = getActiveRecommendations(userId);

        for(Recommendation rec : currentBatch) {
            rec.setScore(null);
        }
    }
}
