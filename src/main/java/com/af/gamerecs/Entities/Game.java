package com.af.gamerecs.entities;

import java.time.LocalDate;
import java.util.Set;

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

    private Set<String> franchises;

    @ElementCollection
    private Set<String> genres;
    
    @ElementCollection
    private Set<String> gameModes;
    
    @ElementCollection
    private Set<String> playerPerspectives;
    
    @ElementCollection
    private Set<String> platforms;

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
                Set<String> genres,
                Set<String> gameModes,
                Set<String> playerPerspectives,
                Set<String> platforms,
                Set<String> keywords,
                Double igdbRating,
                Integer igdbRatingCount) {
        this.igdbId = igdbId;
        this.name = name;
        this.imageId = imageId;
        this.releaseDate = releaseDate;
        this.franchises = franchises;
        this.genres = genres;
        this.gameModes = gameModes;
        this.playerPerspectives = playerPerspectives;
        this.platforms = platforms;
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
    
    public Set<String> getGenres() {
        return genres;
    }
    
    public Set<String> getGameModes() {
        return gameModes;
    }
    
    public Set<String> getPlayerPerspectives() {
        return playerPerspectives;
    }
    
    public Set<String> getPlatforms() {
        return platforms;
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
}
