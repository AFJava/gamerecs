package com.af.gamerecs.service;

import com.af.gamerecs.repositories.RecommendationRepository;

public class RecommendationService {
    public final RecommendationRepository recommendationRepository;

    public RecommendationService(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }
}
