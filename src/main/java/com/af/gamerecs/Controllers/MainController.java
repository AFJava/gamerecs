package com.af.gamerecs.controllers;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.UserGameService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
    public final UserGameService userGameService;
    public final CurrentUserService currentUserService;
    
    public MainController(UserGameService userGameService, CurrentUserService currentUserService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/users/{id}/profile")
    public String profile(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        //Add profile cards for each game added
        Long userId = user.getId();
        List<UserGame> userGames = userGameService.getUserGames(userId);

        //Check if game has already been added by comparing IGDB id
        HashSet<Long> userGamesIgdbIds = new HashSet<>(userGameService.getIgdbIds(userGames));

        model.addAttribute("userGames", userGames);
        model.addAttribute("userGamesIgdbIds", userGamesIgdbIds);
        
        return "profile";
    }
}



