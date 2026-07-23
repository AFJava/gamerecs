package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.af.gamerecs.entities.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {
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
        and rec.rank is not null
        order by rec.rank
            """)
    public List<Recommendation> findAllActiveRecommendations(Long userId);
}
