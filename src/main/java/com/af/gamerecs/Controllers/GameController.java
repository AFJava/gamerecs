package com.af.gamerecs.controllers;

import java.util.List;
import java.util.HashSet;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.dto.SaveGameRequest;
import com.af.gamerecs.dto.SearchResponse;
import com.af.gamerecs.dto.CompanyDto;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.entities.CompanyReference;
import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.service.CompanyReferenceService;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.GameSearchService;
import com.af.gamerecs.service.GameService;
import com.af.gamerecs.service.IgdbService;
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
    public final GameSearchService gameSearchService;
    public final CompanyReferenceService companyReferenceService;
    public int numFeaturesMatched = 5; //Number of features to be used in IGDB request for recommended games

    public GameController(UserGameService userGameService,
                        CurrentUserService currentUserService,
                        GameService gameService,
                        UserPreferenceService userPreferenceService,
                        IgdbService igdbService,
                        GameSearchService gameSearchService,
                        CompanyReferenceService companyReferenceService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
        this.gameService = gameService;
        this.userPreferenceService = userPreferenceService;
        this.igdbService = igdbService;
        this.gameSearchService = gameSearchService;
        this.companyReferenceService = companyReferenceService;
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

        HashSet<Long> addedGamesIgdbIds = new HashSet<>(userGameService.getAddedIgdbIds(userId, gameIgdbIds));

        SearchResponse response = new SearchResponse(games, addedGamesIgdbIds);

        return response;
    }

    @PostMapping("/add")
    public String add(@RequestBody SaveGameRequest saveGameRequest, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        Long igdbId = saveGameRequest.igdbId();
        Integer rating = saveGameRequest.rating();
        IgdbGameDto game = saveGameRequest.game();

        Game g = gameService.getGame(igdbId).orElseGet(() -> gameService.saveGame(gameService.gameFromDto(game)));

        userGameService.saveToProfile(user, g, rating);
        userPreferenceService.updatePreferenceFromGame(user, g, rating);
        
        return "";
    }

    @GetMapping("/rec")
    public String rec(Authentication authentication) {
        //Check user preferences for top scoring features; query IGDB for best matches, use other features to thin out
        Object principal = authentication.getPrincipal();
        
        User user = currentUserService.userFromPrincipal(principal);

        List<UserPreference> preferences = userPreferenceService.getSortedPreferences(user);
        List<UserPreference> topPreferences = preferences.subList(0, numFeaturesMatched);

        //If any top preference is a company, find involved_compnay ids and store as CompanyReference
        for(UserPreference preference : topPreferences) {
            if(preference.getFeature().getFeatureType().isCompany()) {
                //Check if already stored
                List<CompanyReference> references = companyReferenceService.getAllCompanyReferences(
                    preference.getFeature().getIgdbFeatureId(), 
                    preference.getFeature().getFeatureType()
                );

                if(references.isEmpty()) {
                    //If reference was added more than 30 days ago
                    //if(references.get(0).getAdded())

                    List<CompanyDto> involvedCompanies = igdbService.getInvolvedCompanyInstances(
                        preference.getFeature().getIgdbFeatureId()
                    );

                    for(CompanyDto dto : involvedCompanies) {
                        references.add(new CompanyReference(
                            dto.id(),
                            preference.getFeature().getIgdbFeatureId(),
                            gameService.getCompanyRole(dto)
                        ));
                    }

                    companyReferenceService.saveAllCompanyReferences(references);
                }

                //Delay?
                try {
                    Thread.sleep(500); //Pause for 500ms to avoid hitting rate limit
                }
                catch(InterruptedException e) {

                }
            }
        }

        List<IgdbGameDto> topMatches = igdbService.searchMatchingGames(
            userPreferenceService.getFeaturesFromPreferences(topPreferences)
        );

        //System.out.println(topMatches);

        return "";
    }
}
