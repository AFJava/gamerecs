package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Game;

public interface GameRepository extends JpaRepository<UserGame, Integer> {
    List<Game> findByRawgId(Integer rawgId);
}