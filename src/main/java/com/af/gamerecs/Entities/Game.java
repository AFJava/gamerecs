package com.af.gamerecs;
import jakarta.persistence.*;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String genre;
    private String developer;
    private String publisher;
    private int releaseYear;
    
    
    public Game() {

    }
}