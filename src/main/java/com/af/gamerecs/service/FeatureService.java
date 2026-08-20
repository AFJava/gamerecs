package com.af.gamerecs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.repositories.FeatureRepository;

@Service
public class FeatureService {
    private final FeatureRepository featureRepository;

    public FeatureService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<Feature> saveAllFeatures(Set<Feature> features) {
        return featureRepository.saveAll(features);
    }

    public List<Feature> getExistingFeatures(FeatureType featureType, List<Long> igdbIds) {
        return featureRepository.findAllByFeatureTypeAndIgdbFeatureIdIn(featureType, igdbIds);
    }
}
