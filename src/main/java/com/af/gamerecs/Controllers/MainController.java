package com.af.gamerecs.controllers;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.af.gamerecs.dto.IgdbGameDto;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.entities.Recommendation;
import com.af.gamerecs.service.CurrentUserService;
import com.af.gamerecs.service.GameSearchService;
import com.af.gamerecs.service.IgdbService;
import com.af.gamerecs.service.RecommendationService;
import com.af.gamerecs.service.UserGameService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {
    public final UserGameService userGameService;
    public final CurrentUserService currentUserService;
    public final GameSearchService gameSearchService;
    public final IgdbService igdbService;
    public final RecommendationService recommendationService;
    public int pageSize = 10;
    
    public MainController(UserGameService userGameService,
                        CurrentUserService currentUserService,
                        GameSearchService gameSearchService,
                        IgdbService igdbService,
                        RecommendationService recommendationService) {
        this.userGameService = userGameService;
        this.currentUserService = currentUserService;
        this.igdbService = igdbService;
        this.gameSearchService = gameSearchService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/users/{id}/profile")
    public String profile(Model model, Authentication authentication, @PathVariable Long id) {
        Object principal = authentication.getPrincipal();
        User user = currentUserService.userFromPrincipal(principal);
        Long userId = user.getId();
        
        //Add profile cards for each game added
        Page<UserGame> userGames = userGameService.getPaginatedUserGames(userId, PageRequest.of(0, 5));

        //Check if game has already been added by comparing IGDB id
        //HashSet<Long> userGamesIgdbIds = new HashSet<>(userGameService.getIgdbIds(userGames));
        //model.addAttribute("userGamesIgdbIds", userGamesIgdbIds);

        //Check if userGames is longer than 5; if so, take first 5 for display and add button        
        if(userGames.hasNext()) {
            model.addAttribute("expandAdded", true); //If not added, expandAdded = null (false for th:if)
        }
        
        model.addAttribute("userGames", userGames.getContent());
        
        return "profile";
    }

    @GetMapping("/users/{id}/profile/added")
    public String added(Model model, 
                        Authentication authentication,
                        @PathVariable Long id,
                        @RequestParam int page) {
        Page<UserGame> userGamesPage = userGameService.getPaginatedUserGames(id, PageRequest.of(page - 1, pageSize));

        //GetContent() returns List<UserGame>
        model.addAttribute("userGames", userGamesPage.getContent());

        //Page nav logic
        int totalPages = userGamesPage.getTotalPages();

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showFirstPage", startPage > 1);
        model.addAttribute("showLastPage", endPage < totalPages);
        model.addAttribute("showLeftEllipsis", startPage > 2);
        model.addAttribute("showRightEllipsis", endPage < totalPages - 1);

        return "added";
    }

    @GetMapping("/search")
    public String search(Model model,
                        Authentication authentication,
                        @RequestParam String query,
                        @RequestParam(name = "filter-obscure", defaultValue = "false") boolean filterObscure,
                        @RequestParam int page) {
        model.addAttribute("query", query);

        List<IgdbGameDto> games = igdbService.searchGames(query, pageSize, page, filterObscure);
        games = gameSearchService.sortGames(games, query);

        System.out.println(games.size());

        model.addAttribute("games", games);
        
        HashSet<Long> addedGamesIgdbIds = new HashSet<>();
        boolean authenticated = false;

        if(authentication != null) {
            authenticated = true;

            Object principal = authentication.getPrincipal();
            User user = currentUserService.userFromPrincipal(principal);
            Long userId = user.getId();
            
            List<Long> gameIgdbIds = games.stream()
                .map(IgdbGameDto::id)
                .toList();
            
            addedGamesIgdbIds = new HashSet<>(userGameService.getAddedIgdbIds(userId, gameIgdbIds));

            model.addAttribute("id", userId);
        }

        model.addAttribute("authenticated", authenticated);
        model.addAttribute("addedGamesIgdbIds", addedGamesIgdbIds);

        int totalPages = igdbService.numPages(query, pageSize, filterObscure);

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showFirstPage", startPage > 1);
        model.addAttribute("showLastPage", endPage < totalPages);
        model.addAttribute("showLeftEllipsis", startPage > 2);
        model.addAttribute("showRightEllipsis", endPage < totalPages - 1);

        model.addAttribute("filterObscure", filterObscure);
        
        return "search";
    }

    @GetMapping("/users/{id}/profile/recommended")
    public String recommended(Model model,
                            Authentication authentication,
                            @PathVariable Long id,
                            @RequestParam int page) {
        Page<Recommendation> recs = recommendationService.getPaginatedActiveRecommendations(id, PageRequest.of(page - 1, pageSize));
        model.addAttribute("recs", recs.getContent());

        HashSet<Long> sharedIgdbIds = new HashSet<>(recommendationService.getAddedIgdbIds(id));
        model.addAttribute("sharedIgdbIds", sharedIgdbIds);

        int totalPages = recs.getTotalPages();

        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("showFirstPage", startPage > 1);
        model.addAttribute("showLastPage", endPage < totalPages);
        model.addAttribute("showLeftEllipsis", startPage > 2);
        model.addAttribute("showRightEllipsis", endPage < totalPages - 1);

        return "recommended";
    }
}



