package com.af.gamerecs.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usergames")
/* Database entry for usergames table. Directly stores information needed for display card, all else comes from RAWG */
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

    private int rating;

    private boolean favorited;

    public UserGame() {
        favorited = false;
    }

    public UserGame(User user, Game game, int rating) {
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

    public int getRating() {
        return rating;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setFavorited(boolean status) {
        favorited = status;
    }
}