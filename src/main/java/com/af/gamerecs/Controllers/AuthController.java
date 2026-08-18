package com.af.gamerecs.controllers;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.af.gamerecs.entities.Role;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String email, @RequestParam String password, @RequestParam String retype, HttpServletRequest httpRequest, Model model) {
        //First, try loading user from db; verify whether user already exists
        try {
            userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

            //If no exception, user already exists; notify user
            model.addAttribute("signuperr", "User already exists");
            return "login";
        }
        catch(UsernameNotFoundException e) {
            //Else, create new user
            User newUser = new User();
            newUser.setUsername(email);

            if(!retype.equals(password)) {
                System.out.println("Passwords did not match");
                model.addAttribute("signuperr", "Passwords do not match");
                return "login";
            }

            String passwordHash = passwordEncoder.encode(password);
            newUser.setPassword(passwordHash);
            
            newUser.setRole(Role.USER);

            userRepository.save(newUser); //Spring automatically handles SQL INSERT instruction

            //Compare username/password to the user info that was just saved in db
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            //Stores auth info
            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = httpRequest.getSession(true);
            
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            return "redirect:/users/" + newUser.getId() + "/profile";
        }
    }
}