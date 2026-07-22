package com.af.gamerecs.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
    public final GameService gameService;

    public RecommendationService(RecommendationRepository recommendationRepository, GameService gameService) {
        this.gameService = gameService;
        this.recommendationRepository = recommendationRepository;
    }

    public List<Recommendation> getExistingRecommendations(Long userId, List<Long> igdbIds) {
        return recommendationRepository.findAllMatchingUserRecommendations(userId, igdbIds);
    }

    public List<Recommendation> sortRecommendations(User user, List<IgdbGameDto> gameDtos, List<UserPreference> preferences) {
        List<Recommendation> recs = parseRecommendations(user, gameDtos);

        recs.sort(Comparator.comparing(rec -> scoreRecommendation(rec, preferences)));

        return recs;
    }

    public List<Recommendation> parseRecommendations(User user, List<IgdbGameDto> gameDtos) {
        List<Long> igdbIds = gameService.getIgdbIdsFromDtos(gameDtos);
        List<Recommendation> recs = getExistingRecommendations(user.getId(), igdbIds);

        Map<Long, Recommendation> existingRecsMap = recs.stream()
            .collect(Collectors.toMap(
                existingRec -> existingRec.getGame().getIgdbId(),
                existingRec -> existingRec
            ));
        
        for(IgdbGameDto dto : gameDtos) {
            if(! existingRecsMap.keySet().contains(dto.id())) {
                recs.add(new Recommendation(
                    user,
                    gameService.gameFromDto(dto)
                ));
            }
        }

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
}
