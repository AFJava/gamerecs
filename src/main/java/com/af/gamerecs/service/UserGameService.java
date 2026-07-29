package com.af.gamerecs.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

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

    public void saveToProfile(User user, Game game, Integer rating) {
        Long userId = user.getId();
        
        UserGame userGame = getUserGame(userId, game.getIgdbId());
        
        //If game already exists in repository, it was previously favorited; set rating, favorited and overwrite
        if(userGame != null) {
            userGame.setFavorited(false);
            userGame.setRating(rating);
            userGameRepository.save(userGame);

            return;
        }
        
        //Otherwise make new UserGame and save
        userGameRepository.save(new UserGame(user, game, rating));
    }

    public UserGame getUserGame(Long userId, Long igdbId) {
        Optional<UserGame> userGameContainer = userGameRepository.findByUserIdAndGame_IgdbId(userId, igdbId);

        if(userGameContainer.isEmpty()) {
            return null;
        }

        return userGameContainer.get();
    }

    public List<UserGame> getUserGames(Long userId) {
        return userGameRepository.findAllByUserId(userId);
    }
    
    public Page<UserGame> getPaginatedUserGames(Long userId, Pageable pageable) {
        return userGameRepository.findAllByUserId(userId, pageable);
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
