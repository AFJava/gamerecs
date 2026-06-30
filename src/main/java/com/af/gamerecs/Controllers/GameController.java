package com.af.gamerecs.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.SaveGameRequest;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.UserGameService;

@RestController
@RequestMapping("/games")
public class GameController {
    public final UserGameService userGameService;
    public final CurrentUserService currentUserService;

    public GameController(UserGameService userGameService, CurrentUserService currentUserService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/add")
    public String add(@RequestBody SaveGameRequest saveGameRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);
        
        Long rawgId = saveGameRequest.rawgId();
        float rating = saveGameRequest.rating();
        String name = saveGameRequest.name();
        String imageSrc = saveGameRequest.imageSrc();

        userGameService.saveToProfile(user, rawgId, rating, name, imageSrc);
        
        return "";
    }

    @GetMapping("/rec")
    public String rec(Authentication authentication) {
        //Retrieve user id, search for user's added games, get RAWG IDs
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);
        Long userId = user.getId();

        List<UserGame> userGames = userGameService.getUserGames(userId);
        List<Long> rawgIds = userGameService.getRawgIds(userGames);

        //Get details from RAWG

        return "";
    }
}
