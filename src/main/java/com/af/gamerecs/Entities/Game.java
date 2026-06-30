package com.af.gamerecs.entities;

import java.time.LocalDate;
import java.util.List;

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
    private Integer rawgId;

    private String name;

    private String imageSrc;

    @ElementCollection
    private List<String> genres;
    
    @ElementCollection
    private List<String> tags;
    
    @ElementCollection
    private List<String> developers;
    
    @ElementCollection
    private List<String> publishers;

    @ElementCollection
    private List<String> platforms;

    private LocalDate released;

    private Integer metacritic;

    public Game() {

    }

    public Game(Integer rawgId,
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

    /* 
    public Game(Integer rawgId,
                String name,
                String imageSrc,
                List<String> genres,
                List<String> tags,
                List<String> developers,
                List<String> publishers,
                List<String> platforms,
                LocalDate released,
                Integer metacritic) {
        this.rawgId = rawgId;
        this.name = name;
        this.imageSrc = imageSrc;
        this.genres = genres;
        this.tags = tags;
        this.developers = developers;
        this.publishers= publishers;
        this.platforms = platforms;
        this.released = released;
        this.metacritic = metacritic;
    }*/

    public Integer getRawgId() {
        return rawgId;
    }

    public String getName() {
        return name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public List<String> getGenres() {
        return genres;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public List<String> getDevelopers() {
        return developers;
    }
    
    public List<String> getPublishers() {
        return publishers;
    }

    public List<String> getPlatforms() {
        return platforms;
    }

    public LocalDate getReleased() {
        return released;
    }

    public Integer getMetacritic() {
        return metacritic;
    }
}
