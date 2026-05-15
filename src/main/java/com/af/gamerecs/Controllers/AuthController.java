package com.af.gamerecs.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.repositories.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        userRepository.save(user);

        return "User Created";
    }
}