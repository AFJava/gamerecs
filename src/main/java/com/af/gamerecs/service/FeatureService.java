package com.af.gamerecs.service;

import java.util.List;
import java.util.Set;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.repositories.FeatureRepository;

public class FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> saveAllFeatures(Set<Feature> features) {
        return featureRepository.saveAll(features);
    }
}
