package com.af.gamerecs.controllers;

import java.util.HashMap;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.entities.User;
import com.af.gamerecs.repositories.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody HashMap<String, String> userInfo) {
        User newUser = new User();

        String email = userInfo.get("email");
        newUser.setEmail(email);

        String password = userInfo.get("password");
        String passwordHash = passwordEncoder.encode(password);
        newUser.setPasswordHash(passwordHash);
        
        userRepository.save(newUser);

        return "User Created";
    }
}