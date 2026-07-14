package com.af.gamerecs.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.service.IgdbService;

@RestController
@RequestMapping("/api/games")
public class GameApiController {
    private final IgdbService igdbService;

    public GameApiController(IgdbService igdbService) {
        this.igdbService = igdbService;
    }

    /* Endpoint to dynamically retrieve first 5 results from searchbar */
    @GetMapping("/search")
    public List<IgdbGameDto> searchGames(@RequestParam String q, @RequestParam boolean filterObscure) {
        //System.out.println("Sending IGDB request");

        List<IgdbGameDto> games = igdbService.searchGames(q);

        if(filterObscure) {
            //Filter out games that have not been rated at all or ones that do not have any official companies tagged
            games = games.stream()
                .filter(g -> (g.name().equalsIgnoreCase(q)
                        || (g.involved_companies() != null)
                    )   && g.rating() != null
                )
                .toList();
        }

        //Limit to 5 results in either case and keep most relevant results on top
        games = games.stream()
            .sorted((a, b) ->
                Integer.compare(
                    scoreSearchResult(b, q),
                    scoreSearchResult(a, q)
                )
            )
            .toList();
        
        return games;
    }

    /* Show high scoring results first; only name matching, for now */
    private int scoreSearchResult(IgdbGameDto game, String query) {
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

        return score;
    }
}
