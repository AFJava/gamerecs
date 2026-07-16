package com.af.gamerecs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.af.gamerecs.dto.IgdbGameDto;

@Service
public class GameSearchService {
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

    public List<IgdbGameDto> sortGames(List<IgdbGameDto> games, String query, boolean filterObscure) { 
        if(filterObscure) {
            //Filter out games that have not been rated at all or ones that do not have any official companies tagged
            games = games.stream()
                .filter(g -> (g.name().equalsIgnoreCase(query)
                        || (g.involved_companies() != null)
                    )   && g.rating() != null
                )
                .toList();
        }

        //Limit to 5 results in either case and keep most relevant results on top
        games = games.stream()
            .sorted((a, b) ->
                Integer.compare(
                    scoreSearchResult(b, query),
                    scoreSearchResult(a, query)
                )
            )
            .toList();

        return games;
    }
}
