package com.af.gamerecs.controllers;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("_csrf", token);

        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        return "profile";
    }
}



