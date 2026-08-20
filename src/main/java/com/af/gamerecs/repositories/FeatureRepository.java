package com.af.gamerecs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.FeatureType;

public interface FeatureRepository extends JpaRepository<Feature, Integer> {
    public List<Feature> findAllByIgdbFeatureIdAndFeatureTypeIn(List<Long> igdbIds, FeatureType featureType);
}
