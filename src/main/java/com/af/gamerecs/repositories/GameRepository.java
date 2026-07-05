package com.af.gamerecs.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Game;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findByIgdbId(Long igdbId);

    @EntityGraph(attributePaths = {
        "franchises",
        "genres",
        "themes",
        "gameModes",
        "playerPerspectives",
        "platforms",
        "keywords"
    })
    List<Game> findAllByIgdbIdIn(List<Long> igdbIds);
}