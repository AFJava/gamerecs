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

    @ElementCollection
    private Set<Feature> features;

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
                Set<Feature> features,
                Double igdbRating,
                Integer igdbRatingCount) {
        this.igdbId = igdbId;
        this.name = name;
        this.imageId = imageId;
        this.releaseDate = releaseDate;
        this.features = features;
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
    
    public Set<Feature> getFeatures() {
        return features;
    }

    public Double getIgdbRating() {
        return igdbRating;
    }

    public Integer getIgdbRatingCount() {
        return igdbRatingCount;
    }
}
