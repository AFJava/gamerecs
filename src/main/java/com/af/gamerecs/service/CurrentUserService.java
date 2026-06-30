package com.af.gamerecs.service;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.repositories.UserRepository;

@Service
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User userFromPrincipal(Object principal) {
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

        return user;
    }
}
