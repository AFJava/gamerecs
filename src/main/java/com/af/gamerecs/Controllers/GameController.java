package com.af.gamerecs.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.dto.SaveGameRequest;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.GameService;
import com.af.gamerecs.service.UserGameService;
import com.af.gamerecs.service.UserPreferenceService;

@RestController
@RequestMapping("/games")
public class GameController {
    public final UserGameService userGameService;
    public final CurrentUserService currentUserService;
    public final GameService gameService;
    public final UserPreferenceService userPreferenceService;

    public GameController(UserGameService userGameService, CurrentUserService currentUserService, GameService gameService, UserPreferenceService userPreferenceService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
        this.gameService = gameService;
        this.userPreferenceService = userPreferenceService;
    }

    @PostMapping("/add")
    public String add(@RequestBody SaveGameRequest saveGameRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        Long igdbId = saveGameRequest.igdbId();
        Integer rating = saveGameRequest.rating();
        IgdbGameDto igdbGameDto = saveGameRequest.game();

        Game g = gameService.getGame(igdbId).orElseGet(() -> gameService.saveGame(gameService.gameFromDto(igdbGameDto)));

        userGameService.saveToProfile(user, g, rating);
        userPreferenceService.updatePreferenceFromGame(user, g, rating);
        
        return "";
    }

    @GetMapping("/rec")
    public String rec(Authentication authentication) {
        //Check user preferences for top scoring features; query IGDB for best matches, use other features to thin out
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);
        Long userId = user.getId();

        List<UserGame> userGames = userGameService.getUserGames(userId);
        List<Long> igdbIds = userGameService.getIgdbIds(userGames);

        List<Game> games = gameService.getGamesFromIgdbIds(igdbIds);

        return "";
    }
}
