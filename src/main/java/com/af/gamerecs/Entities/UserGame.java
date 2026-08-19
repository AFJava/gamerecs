package com.af.gamerecs.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_games",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_game",
            columnNames = {"user_id", "game_id"}
        )
    }
)
/* Database entry for usergames table. */
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private double rating;

    private boolean favorited;

    public UserGame() {
        favorited = false;
    }

    public UserGame(User user, Game game, double rating) {
        favorited = false;
        this.user = user;
        this.game = game;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public double getRating() {
        return rating;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setFavorited(boolean status) {
        favorited = status;
    }
}