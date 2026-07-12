package com.af.gamerecs.controllers;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        //Check if userGames is longer than 5; if so, take first 5 for display and add button        
        if(userGames.size() > 5) {
            userGames = userGames.subList(0, 5);
            model.addAttribute("expandAdded", true); //If not added, expandAdded = null (false for th:if)
        }
        
        model.addAttribute("userGames", userGames);
        model.addAttribute("userGamesIgdbIds", userGamesIgdbIds);
        
        return "profile";
    }

    @GetMapping("/users/{id}/profile/added")
    public String added(Model model, Authentication authentication, @RequestParam int page) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        //Add profile cards for each game added
        Long userId = user.getId();
        List<UserGame> userGames = userGameService.getUserGames(userId);

        //Separate into 10 depending on page
        userGames = userGames.subList(10 * (page - 1), Math.min(userGames.size(), 10 * page));

        model.addAttribute("userGames", userGames);

        return "added";
    }
}



