package com.af.gamerecs.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.RawgGameDto;
import com.af.gamerecs.dto.RawgSearchResponse;
import com.af.gamerecs.service.RawgService;

@RestController
@RequestMapping("/api/games")
public class GameApiController {
    private final RawgService rawgService;

    public GameApiController(RawgService rawgService) {
        this.rawgService = rawgService;
    }

    /* Endpoint to dynamically retrieve first 5 results from searchbar */
    @GetMapping("/search")
    public RawgSearchResponse searchGames(@RequestParam String q, @RequestParam boolean filterObscure) {
        RawgSearchResponse response = rawgService.searchGames(q);

        if(filterObscure) {
            List<RawgGameDto> games = response.results();

            //For now, keep all games that have been added on RAWG over 100 times
            List<RawgGameDto> filteredGames = games.stream()
                .filter(g -> g.added() >= 100)
                .toList();

            response = new RawgSearchResponse(
                response.count(),
                response.next(),
                response.previous(),
                filteredGames
            );
        }
        
        return response;
    }
}
