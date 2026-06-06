package com.af.gamerecs.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.af.gamerecs.service.RawgService;

@RestController
@RequestMapping("/api/games")
public class GameApiController {
    private final RawgService rawgService;

    public GameApiController(RawgService rawgService) {
        this.rawgService = rawgService;
    }

    @GetMapping("/search")
    public String searchGames(@RequestParam String q) {
        return rawgService.searchGames(q);
    }
}
