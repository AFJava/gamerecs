package com.af.gamerecs.service;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.repositories.UserGameRepository;

@Service
public class UserGameService {
    public final UserGameRepository userGameRepository;
    
    public UserGameService(UserGameRepository userGameRepository) {
        this.userGameRepository = userGameRepository;
    }

    public void saveToProfile(User user, Long rawgId, float rating, String name, String imageSrc) throws IllegalArgumentException {
        Long userId = user.getId();
        
        //Do not save if game has already been added (has same userId and rawgId)
        if(userGameRepository.existsByUserIdAndRawgId(userId, rawgId)) {
            throw new IllegalArgumentException("Game Already Saved");
        }
        
        UserGame userGame = new UserGame(user, rawgId, rating, name, imageSrc);
        userGameRepository.save(userGame);
    }
}
