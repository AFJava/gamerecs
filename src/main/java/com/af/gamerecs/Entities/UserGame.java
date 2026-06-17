package com.af.gamerecs.entities;
import jakarta.persistence.Column;
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
    
    @Column(name = "rawg_id", nullable = false)
    private Long rawgId;

    private String name;

    private String imageSrc;

    private float rating;


    public UserGame() {

    }

    public UserGame(User user, Long rawgId, float rating, String name, String imageSrc) {
        this.user = user;
        this.rawgId = rawgId;
        this.name = name;
        this.imageSrc = imageSrc;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Long getRawgId() {
        return rawgId;
    }

    public String getName() {
        return name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public float getRating() {
        return rating;
    }
}