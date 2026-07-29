package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.af.gamerecs.entities.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
    @Query("""
        select rec
        from Recommendation rec
        where rec.user.id = :userId
        and rec.game.igdbId = :igdbId
    """)
    public Recommendation findRecommendation(Long userId, Long igdbId);

    @Query("""
        select rec
        from Recommendation rec
        where rec.user.id = :userId
        and rec.game.igdbId in :igdbIds
    """)
    public List<Recommendation> findAllMatchingUserRecommendations(Long userId, List<Long> igdbIds);

    @Query("""
        select rec
        from Recommendation rec
        where rec.user.id = :userId
        and rec.score is not null
        order by rec.score
            """)
    public List<Recommendation> findAllActiveRecommendations(Long userId);

    @Query("""
        select rec
        from Recommendation rec
        where rec.user.id = :userId
        and rec.score is not null
        order by rec.score
            """)
    public Page<Recommendation> findAllActiveRecommendations(Long userId, Pageable pageable);

    @Query("""
        select rec.game.igdbId
        from Recommendation rec
        join UserGame ug
            on ug.user.id = rec.user.id
            and ug.game.igdbId = rec.game.igdbId
        where rec.score is not null
            and rec.user.id = :userId
    """)
    public List<Long> findAddedIgdbIds(Long userId);

    @Query("""
        select rec.game.igdbId
        from Recommendation rec
        join UserGame ug
            on rec.user.id = ug.user.id
            and rec.game.igdbId = ug.game.igdbId
        where ug.favorited
            and rec.user.id = :userId
            and rec.score is not null
    """)
    public List<Long> findFavoritedIgdbIds(Long userId);
}
