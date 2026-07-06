package com.af.gamerecs.service;

import com.af.gamerecs.entities.Game;

public class RecommendationService {
    private final CurrentUserService currentUserService;

    public RecommendationService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    /* To be called each time a game is added */
    public void updateWeights(Game game) {
        
    }
}
