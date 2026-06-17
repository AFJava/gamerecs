package com.af.gamerecs.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "usergames")
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "rawg_id", nullable = false)
    private Long rawgId;

    public UserGame() {

    }

    public UserGame(Long userId, Long rawgId) {
        this.userId = userId;
        this.rawgId = rawgId;
    }

    public Long getRawgId() {
        return rawgId;
    }
}