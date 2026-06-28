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
        List<RawgGameDto> games = response.results();

        if(filterObscure) {
            //Filter out games that do not have an official metacritic rating or have been added <100 times on RAWG (unless exact name match)
            games = games.stream()
                .filter(g -> g.name().equalsIgnoreCase(q)
                    || g.metacritic() != null
                    || (g.added() != null && g.added() >= 100)
                )
                .toList();
        }

        //Limit to 5 results in either case
        games = games.stream()
            .sorted((a, b) ->
                Integer.compare(
                    scoreSearchResult(b, q),
                    scoreSearchResult(a, q)
                )
            )
            .limit(5)
            .toList();

        response = new RawgSearchResponse(
            response.count(),
            response.next(),
            response.previous(),
            games
        );
        
        return response;
    }

    /* Show high scoring results first; only name matching, for now */
    private int scoreSearchResult(RawgGameDto game, String query) {
        int score = 0;
        String name = game.name().toLowerCase();
        String q = query.toLowerCase();

        if(name.equals(q)) {
            score += 1000;
        }
        else if(name.startsWith(q)) {
            score += 800;
        }
        else if(name.contains(q)) {
            score += 600;
        }

        //Add popularity filters (redundant?)

        return score;
    }
}
