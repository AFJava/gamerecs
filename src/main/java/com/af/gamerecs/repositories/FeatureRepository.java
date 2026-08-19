package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {
    public List<Feature> findAllByIgdbFeatureIdIn(List<Long> igdbIds);
}
