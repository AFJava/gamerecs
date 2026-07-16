package com.af.gamerecs.service;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.repositories.UserGameRepository;

@Service
public class UserGameService {
    public final UserGameRepository userGameRepository;
    
    public UserGameService(UserGameRepository userGameRepository) {
        this.userGameRepository = userGameRepository;
    }

    public void saveToProfile(User user, Game game, Integer rating) throws IllegalArgumentException {
        Long userId = user.getId();
        
        //Do not save if game has already been added (has same userId and rawgId)
        if(userGameRepository.existsByUserIdAndGame_IgdbId(userId, game.getIgdbId())) {
            throw new IllegalArgumentException("Game Already Saved");
        }
        
        UserGame userGame = new UserGame(user, game, rating);
        userGameRepository.save(userGame);
    }

    public List<UserGame> getUserGames(Long userId) {
        return userGameRepository.findByUserId(userId);
    }
    
    public Page<UserGame> getPaginatedUserGames(Long userId, Pageable pageable) {
        return userGameRepository.findByUserId(userId, pageable);
    }

    public List<Long> getIgdbIds(List<UserGame> userGames) {
        List<Long> igdbIds = new ArrayList<>();
        
        for(UserGame userGame : userGames) {
            igdbIds.add(userGame.getGame().getIgdbId());
        }

        return igdbIds;
    }
    
    public List<Long> getAddedIgdbIds(Long userId, List<Long> IgdbIds) {
        return userGameRepository.findAddedIgdbIds(userId, IgdbIds);
    }
}
