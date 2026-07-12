package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.UserGame;

public interface UserGameRepository extends JpaRepository<UserGame, Integer> {
    boolean existsByUserIdAndGame_IgdbId(Long userId, Long IgdbId);
    List<UserGame> findByUserId(Long userId);
    Page<UserGame> findByUserId(Long userId, Pageable pageable);
}
