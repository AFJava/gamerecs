package com.af.gamerecs.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.af.gamerecs.entities.UserGame;

public interface UserGameRepository extends JpaRepository<UserGame, Integer> {
    boolean existsByUserIdAndGame_IgdbId(Long userId, Long IgdbId);
    Optional<UserGame> findByUserIdAndGame_IgdbId(Long userId, Long igdbId);

    @Query("""
        select ug
        from UserGame ug
        where ug.user.id = :userId
          and not ug.favorited
    """)
    List<UserGame> findAllAddedByUserId(Long userId);

    @Query("""
        select ug
        from UserGame ug
        where ug.user.id = :userId
          and not ug.favorited
    """)
    Page<UserGame> findAllAddedByUserId(Long userId, Pageable pageable);

    //Check whether IGDB IDs from search results match any from UserGames
    @Query("""
    select ug.game.igdbId
    from UserGame ug
    where ug.user.id = :userId
      and ug.game.igdbId in :IgdbIds
    """)
    List<Long> findAddedIgdbIds(Long userId, List<Long> IgdbIds);
}
