package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.UserGame;

public interface UserGameRepository extends JpaRepository<UserGame, Integer> {
    boolean existsByUserIdAndGame_RawgId(Long userId, Long rawgId);
    List<UserGame> findByUserId(Long userId);
}
