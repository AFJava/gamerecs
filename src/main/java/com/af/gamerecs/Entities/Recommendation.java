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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "recommendations",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_user_game",
            columnNames = {"user_id", "game_id"}
        )
    }
)
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
    private Double score;

    private boolean impression;

    public Recommendation() {
        lastSeen = LocalDateTime.now();
        impression = false;
        pressure = 0.0;
    }

    public Recommendation(User user, Game game) {
        lastSeen = LocalDateTime.now();
        impression = false;
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

    public double getScore() {
        return score;
    }

    public boolean getImpression() {
        return impression;
    }

    //Calculate decay to get current pressure
    public void updatePressure() {
        LocalDateTime now = LocalDateTime.now();
        Duration elapsed = Duration.between(lastSeen, now);

        setLastSeen(now);

        //Halves in 1 day
        pressure = pressure * Math.pow(0.5, elapsed.toSeconds() / 86400.0);
    }

    //Update pressure due to decay, then add pressure
    public void logImpression() {
        updatePressure();

        pressure += 100;

        setImpression(true);
    }

    public void setLastSeen(LocalDateTime now) {
        this.lastSeen = now;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setImpression(boolean status) {
        this.impression = status;
    }
}
