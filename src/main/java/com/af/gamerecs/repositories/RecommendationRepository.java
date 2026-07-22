package com.af.gamerecs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Recommendation;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer>{

}
