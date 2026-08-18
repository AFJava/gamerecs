package com.af.gamerecs.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.dto.ImpressionData;
import com.af.gamerecs.dto.SaveGameRequest;
import com.af.gamerecs.dto.SearchResponse;
import com.af.gamerecs.dto.FavGameRequest;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.Recommendation;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.exception.IgdbRateLimitException;
import com.af.gamerecs.service.CompanyReferenceService;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.GameSearchService;
import com.af.gamerecs.service.GameService;
import com.af.gamerecs.service.IgdbQueryBuilder;
import com.af.gamerecs.service.IgdbService;
import com.af.gamerecs.service.RecommendationService;
import com.af.gamerecs.service.UserGameService;
import com.af.gamerecs.service.UserPreferenceService;

@RestController
@RequestMapping("/games")
public class GameController {
    public final UserGameService userGameService;
    public final CurrentUserService currentUserService;
    public final GameService gameService;
    public final UserPreferenceService userPreferenceService;
    public final IgdbService igdbService;
    public final IgdbQueryBuilder igdbQueryBuilder;
    public final GameSearchService gameSearchService;
    public final CompanyReferenceService companyReferenceService;
    public final RecommendationService recommendationService;
    public int numFeaturesMatched = 10; //Number of features to be used in initial IGDB request for recommended games
    public int numFeaturesMatchedScore = 30; //Number of features to be used when scoring, sorting recommended games
    public double favGameRating = 7.5; //Rating given to all favorited games by default

    public GameController(UserGameService userGameService,
                        CurrentUserService currentUserService,
                        GameService gameService,
                        UserPreferenceService userPreferenceService,
                        IgdbService igdbService,
                        IgdbQueryBuilder igdbQueryBuilder,
                        GameSearchService gameSearchService,
                        CompanyReferenceService companyReferenceService,
                        RecommendationService recommendationService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
        this.gameService = gameService;
        this.userPreferenceService = userPreferenceService;
        this.igdbService = igdbService;
        this.igdbQueryBuilder = igdbQueryBuilder;
        this.gameSearchService = gameSearchService;
        this.companyReferenceService = companyReferenceService;
        this.recommendationService = recommendationService;
    }
    
    @ExceptionHandler
    public ResponseEntity<String> handleIgdbRateLimit(IgdbRateLimitException e) {
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body("IGDB rate limit exceeded");
    }

    /* Endpoint to dynamically results for searchbar */
    @GetMapping("/search")
    public SearchResponse searchGames(Authentication authentication, @RequestParam String q, @RequestParam boolean filterObscure) {
        //System.out.println("Sending IGDB request");

        List<IgdbGameDto> games = igdbService.searchGames(q, filterObscure);
        games = gameSearchService.sortGames(games, q);

        Object principal = authentication.getPrincipal();
        User user = currentUserService.userFromPrincipal(principal);
        Long userId = user.getId();

        List<Long> gameIgdbIds = games.stream()
            .map(IgdbGameDto::id)
            .toList();

        Set<Long> addedGamesIgdbIds = userGameService.getAddedIgdbIds(userId, gameIgdbIds);
        Set<Long> favoritedGamesIgdbIds = userGameService.getFavoritedIgdbIds(userId, gameIgdbIds);

        SearchResponse response = new SearchResponse(games, addedGamesIgdbIds, favoritedGamesIgdbIds);

        return response;
    }

    @PostMapping("/add")
    public String add(@RequestBody SaveGameRequest saveGameRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        user.setRequestCount(null);
        currentUserService.saveUser(user);

        Long igdbId = saveGameRequest.igdbId();
        Double rating = saveGameRequest.rating();
        IgdbGameDto gameDto = saveGameRequest.game();

        Game game = gameService.getGame(igdbId).orElseGet(() -> gameService.saveGame(gameService.gameFromDto(gameDto)));

        userGameService.saveToProfile(new UserGame(user, game, rating));
        userPreferenceService.updatePreferenceFromGame(user, game, rating);
        
        return "";
    }

    @PostMapping("/rec")
    public String rec(Authentication authentication) {
        //Check user preferences for top scoring features; query IGDB for best matches, use other features to thin out
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        List<UserPreference> preferences = userPreferenceService.getSortedPreferences(user);
        List<UserPreference> topPreferences = preferences.subList(
            0, 
            Math.min(numFeaturesMatched, 
                preferences.size()
            )
        );

        //If any top preference is a company, find involved_compnay ids and store as CompanyReference
        companyReferenceService.saveAllCompanyReferences(topPreferences);

        String params = igdbQueryBuilder.parseParams(
            userPreferenceService.getFeaturesFromPreferences(topPreferences)
        );

        Integer count = user.getRequestCount();
        if(count == null) {
            count = igdbService.countMatchingGames(params);

            user.setRequestCount(count);
            currentUserService.saveUser(user);
        }

        List<IgdbGameDto> topMatches = igdbService.searchMatchingGames(params, count);
        recommendationService.sortRecommendations(user, topMatches, preferences.subList(0, numFeaturesMatchedScore));
        
        /*
        for(Recommendation rec : recs) {
            System.out.println(rec.getGame().getName());
        }
        */
        
        //System.out.println(topMatches);

        return "";
    }

    @PostMapping("/favorite")
    public String fav(Authentication authentication, @RequestBody FavGameRequest favGameRequest) {
        Object principal = authentication.getPrincipal();
        User user = currentUserService.userFromPrincipal(principal);

        user.setRequestCount(null);
        currentUserService.saveUser(user);

        Long igdbId = favGameRequest.igdbId();
        IgdbGameDto gameDto = favGameRequest.game();
        Game game = gameService.getGame(igdbId).orElseGet(() -> gameService.saveGame(gameService.gameFromDto(gameDto)));

        UserGame userGame = new UserGame(user, game, favGameRating);
        userGame.setFavorited(true);

        userGameService.saveToProfile(userGame);
        userPreferenceService.updatePreferenceFromGame(user, game, favGameRating);
        
        return "";
    }

    @DeleteMapping("/remove/{igdbId}") 
    public String remove(Authentication authentication, @PathVariable Long igdbId) {
        Object principal = authentication.getPrincipal();
        User user = currentUserService.userFromPrincipal(principal);

        Long userId = user.getId();

        user.setRequestCount(null);
        currentUserService.saveUser(user);

        UserGame userGame = userGameService.removeUserGame(userId, igdbId);
        userGame.setRating(-1 * userGame.getRating());
        
        userPreferenceService.updatePreferenceFromGame(userGame);

        return "";
    }

    //Client only sends ids of games in the current recommendation batch that have NOT made an impression yet
    @PostMapping("/rec/impression")
    public String impression(Authentication authentication, @RequestBody ImpressionData impressionData) {
        Object principal = authentication.getPrincipal();
        User user = currentUserService.userFromPrincipal(principal);

        Long userId = user.getId();
        
        List<Long> impressionGameIds = impressionData.impressionGameIds();
        List<Recommendation> impressionGames = recommendationService.getExistingRecommendations(userId, impressionGameIds);
        
        //System.out.println("Impression data received");

        for(Recommendation rec : impressionGames) {
            rec.logImpression();
        }

        recommendationService.saveAllRecommendations(impressionGames);
        
        //System.out.println("Impression data saved");

        return "";
    }
}
