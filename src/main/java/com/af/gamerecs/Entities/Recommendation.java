package com.af.gamerecs.entities;

import java.time.LocalDateTime;
import java.time.Duration;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private LocalDateTime lastSeen;

    //Add pressure every time this recommendation is seen; pressure decays exponentially
    private Double pressure;

    //Not part of current recommendation batch if null
    private Integer rank;

    public Recommendation() {
        pressure = 0.0;
    }

    public Recommendation(User user, Game game) {
        pressure = 0.0;
        this.user = user;
        this.game = game;
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

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public Double getPressure() {
        return pressure;
    }

    public Integer getRank() {
        return rank;
    }

    public void updatePressure() {
        LocalDateTime now = LocalDateTime.now();

        Duration elapsed = Duration.between(lastSeen, now);

        //Once recommendation is seen, calculate decay
        pressure = pressure * Math.pow(0.5, elapsed.toMinutes() / 1440) + 50;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}
