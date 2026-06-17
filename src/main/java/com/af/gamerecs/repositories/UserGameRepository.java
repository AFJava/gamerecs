package com.af.gamerecs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.UserGame;

public interface UserGameRepository extends JpaRepository<UserGame, Integer> {
    boolean existsByUserIdAndRawgId(Long userId, Long rawgId);
}
