package com.af.gamerecs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.Recommendation;
import com.af.gamerecs.repositories.RecommendationRepository;

@Service
public class RecommendationService {
    public final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    public List<Recommendation> getExistingRecommendations(Long userId, List<Long> igdbIds) {
        return recommendationRepository.findAllMatchingUserRecommendations(userId, igdbIds);
    }
}
