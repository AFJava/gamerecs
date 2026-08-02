package com.af.gamerecs.service;

import java.util.List;
import java.util.Set;
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

    public void saveToProfile(UserGame userGame) {
        Long userId = userGame.getUser().getId();
        
        UserGame existingUserGame = getUserGame(userId, userGame.getGame().getIgdbId());
        
        //If game already exists in repository, it was previously favorited; set rating, favorited
        if(existingUserGame != null) {
            existingUserGame.setFavorited(false);
            existingUserGame.setRating(userGame.getRating());

            userGame = existingUserGame;
        }
        
        //Otherwise make new UserGame and save
        userGameRepository.save(userGame);
    }

    public void removeUserGame(Long userId, Long igdbId) {
        userGameRepository.deleteByUserIdAndGame_IgdbId(userId, igdbId);
    }

    public UserGame getUserGame(Long userId, Long igdbId) {
        Optional<UserGame> userGameContainer = userGameRepository.findByUserIdAndGame_IgdbId(userId, igdbId);

        if(userGameContainer.isEmpty()) {
            return null;
        }

        return userGameContainer.get();
    }

    public List<UserGame> getAddedGames(Long userId) {
        return userGameRepository.findAllAddedByUserId(userId);
    }
    
    public Page<UserGame> getPaginatedAddedGames(Long userId, Pageable pageable) {
        return userGameRepository.findAllAddedByUserId(userId, pageable);
    }

    public List<UserGame> getFavoritedGames(Long userId) {
        return userGameRepository.findAllFavoritedByUserId(userId);
    }
    
    public Page<UserGame> getPaginatedFavoritedGames(Long userId, Pageable pageable) {
        return userGameRepository.findAllFavoritedByUserId(userId, pageable);
    }

    public List<Long> getIgdbIds(List<UserGame> userGames) {
        List<Long> igdbIds = new ArrayList<>();
        
        for(UserGame userGame : userGames) {
            igdbIds.add(userGame.getGame().getIgdbId());
        }

        return igdbIds;
    }
    
    public Set<Long> getAddedIgdbIds(Long userId, List<Long> IgdbIds) {
        return userGameRepository.findAddedIgdbIds(userId, IgdbIds);
    }

    public Set<Long> getFavoritedIgdbIds(Long userId, List<Long> IgdbIds) {
        return userGameRepository.findFavoritedIgdbIds(userId, IgdbIds);
    }
}
