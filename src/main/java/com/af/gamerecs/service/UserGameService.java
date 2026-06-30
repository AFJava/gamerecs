package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.entities.Game;
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
        if(userGameRepository.existsByUserIdAndGame_RawgId(userId, game.getRawgId())) {
            throw new IllegalArgumentException("Game Already Saved");
        }
        
        UserGame userGame = new UserGame(user, game, rating);
        userGameRepository.save(userGame);
    }

    public List<UserGame> getUserGames(Long userId) {
        return userGameRepository.findByUserId(userId);
    }

    public List<Integer> getRawgIds(List<UserGame> userGames) {
        List<Integer> rawgIds = new ArrayList<>();
        
        for(UserGame userGame : userGames) {
            rawgIds.add(userGame.getGame().getRawgId());
        }

        return rawgIds;
    }
}
