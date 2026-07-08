package com.af.gamerecs.entities;

import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long igdbId;

    private String name;

    private String imageId;
    
    private LocalDate releaseDate;

    @ElementCollection
    private Set<String> franchises;
    
    @ElementCollection
    private Set<String> companies;
    
    @ElementCollection
    private Set<String> platforms;
    
    @ElementCollection
    private Set<String> genres;

    @ElementCollection
    private Set<String> themes;
    
    @ElementCollection
    private Set<String> gameModes;
    
    @ElementCollection
    private Set<String> playerPerspectives;

    @ElementCollection
    private Set<String> keywords;

    private Double igdbRating;

    private Integer igdbRatingCount;

    public Game() {

    }
    
    /*
    public Game(Long rawgId,
                String name,
                String imageSrc,
                LocalDate released,
                Integer metacritic) {
        this.rawgId = rawgId;
        this.name = name;
        this.imageSrc = imageSrc;
        this.released = released;
        this.metacritic = metacritic;
    }
    */
    
    public Game(Long igdbId,
                String name,
                String imageId,
                LocalDate releaseDate,
                Set<String> franchises,
                Set<String> companies,
                Set<String> platforms,
                Set<String> genres,
                Set<String> themes,
                Set<String> gameModes,
                Set<String> playerPerspectives,
                Set<String> keywords,
                Double igdbRating,
                Integer igdbRatingCount) {
        this.igdbId = igdbId;
        this.name = name;
        this.imageId = imageId;
        this.releaseDate = releaseDate;
        this.franchises = franchises;
        this.companies = companies;
        this.platforms = platforms;
        this.genres = genres;
        this.themes = themes;
        this.gameModes = gameModes;
        this.playerPerspectives = playerPerspectives;
        this.keywords = keywords;
        this.igdbRating = igdbRating;
        this.igdbRatingCount = igdbRatingCount;
    }

    public Long getIgdbId() {
        return igdbId;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }
    
    public LocalDate getReleased() {
        return releaseDate;
    }

    public Set<String> getFranchises() {
        return franchises;
    }

    public Set<String> getCompanies() {
        return companies;
    }
        
    public Set<String> getPlatforms() {
        return platforms;
    }

    public Set<String> getGenres() {
        return genres;
    }

    public Set<String> getThemes() {
        return themes;
    }
    
    public Set<String> getGameModes() {
        return gameModes;
    }
    
    public Set<String> getPlayerPerspectives() {
        return playerPerspectives;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public Double getIgdbRating() {
        return igdbRating;
    }

    public Integer getIgdbRatingCount() {
        return igdbRatingCount;
    }

    public List<Feature> getFeatures() {
        List<Feature> features = new ArrayList<>();
        
        for(String franchise : franchises) {
            features.add(new Feature(FeatureType.FRANCHISE, franchise));
        }

        for(String company : companies) {
            features.add(new Feature(FeatureType.COMPANY, company));
        }

        for(String platform : platforms) {
            features.add(new Feature(FeatureType.PLATFORM, platform));
        }

        for(String genre : genres) {
            features.add(new Feature(FeatureType.GENRE, genre));
        }

        for(String theme : themes) {
            features.add(new Feature(FeatureType.THEME, theme));
        }

        for(String gameMode : gameModes) {
            features.add(new Feature(FeatureType.GAME_MODE, gameMode));
        }

        for(String playerPerspective : playerPerspectives) {
            features.add(new Feature(FeatureType.PLAYER_PERSPECTIVE, playerPerspective));
        }

        for(String keyword : keywords) {
            features.add(new Feature(FeatureType.KEYWORD, keyword));
        }

        return features;
    }
}
