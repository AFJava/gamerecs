package com.af.gamerecs.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByRawgId(Long rawgId);
}