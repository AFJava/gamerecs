package com.af.gamerecs.controllers;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.af.gamerecs.repositories.UserRepository;
import com.af.gamerecs.dto.SaveGameRequest;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.service.UserGameService;

@RestController
@RequestMapping("/games")
public class GameController {
    public final UserGameService userGameService;
    public final UserRepository userRepository;

    public GameController(UserGameService userGameService, UserRepository userRepository) {
        this.userGameService = userGameService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public String add(@RequestBody SaveGameRequest saveGameRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user;

        if(principal instanceof User) {
            user = (User) principal;
        }
        else if(principal instanceof OAuth2User oauthUser) {
            String email = oauthUser.getAttribute("email");

            user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("User not found"));
        }
        else {
            throw new IllegalStateException(
                    "Unsupported principal type: " + principal.getClass());
        }
        
        Long rawgId = saveGameRequest.rawgId();
        float rating = saveGameRequest.rating();
        String name = saveGameRequest.name();
        String imageSrc = saveGameRequest.imageSrc();

        userGameService.saveToProfile(user, rawgId, rating, name, imageSrc);
        
        return "Added";
    }
}
