package com.af.gamerecs.service;

import org.springframework.stereotype.Service;

import com.af.gamerecs.repositories.UserGameRepository;
import com.af.gamerecs.entities.UserGame;

@Service
public class UserGameService {
    public final UserGameRepository userGameRepository;
    
    public UserGameService(UserGameRepository userGameRepository) {
        this.userGameRepository = userGameRepository;
    }

    public void saveToProfile(Long userId, Long rawgId) throws IllegalArgumentException {
        //Do not save if game has already been added (has same userId and rawgId)
        if(userGameRepository.existsByUserIdAndRawgId(userId, rawgId)) {
            throw new IllegalArgumentException("Game Already Saved");
        }

        userGameRepository.save(new UserGame(userId, rawgId));
    }
}
