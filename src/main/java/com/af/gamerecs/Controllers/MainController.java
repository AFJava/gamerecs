package com.af.gamerecs.controllers;

import java.util.HashSet;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.repositories.UserGameRepository;
import com.af.gamerecs.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
    public final UserGameRepository userGameRepository;
    public final UserRepository userRepository;
    
    public MainController(UserGameRepository userGameRepository, UserRepository userRepository) {
        this.userGameRepository = userGameRepository;
        this.userRepository = userRepository;
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
        User user;

        Object principal = authentication.getPrincipal();

        if(principal instanceof User) {
            user = (User) principal;
        } else if (principal instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");

            user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
        }
        else {
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
        }

        //Add profile cards for each game added
        Long userId = user.getId();
        List<UserGame> userGames = userGameRepository.findByUserId(userId);

        //Check if game has already been added by comparing RAWG id
        HashSet<Long> userGamesRawgIds = new HashSet<>();
        for(UserGame game : userGames) {
            userGamesRawgIds.add(game.getRawgId());
        }

        model.addAttribute("userGames", userGames);
        model.addAttribute("userGamesRawgIds", userGamesRawgIds);
        
        return "profile";
    }
}



