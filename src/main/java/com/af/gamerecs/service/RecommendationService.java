package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.Recommendation;
import com.af.gamerecs.entities.User;
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

    public List<Recommendation> sortRecommendations(User user, List<IgdbGameDto> gameDtos) {
        return new ArrayList<>();
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
}
