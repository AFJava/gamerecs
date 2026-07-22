package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.Recommendation;
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

    public List<Recommendation> sortRecommendations(List<IgdbGameDto> gameDtos) {
        return new ArrayList<>();
    }
}
