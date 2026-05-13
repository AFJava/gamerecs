package com.af.gamerecs;
import jakarta.persistence.*;

@Entity
@Table(name = "usergames")
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    

    public UserGame() {

    }
}