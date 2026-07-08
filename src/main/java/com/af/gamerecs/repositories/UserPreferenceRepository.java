package com.af.gamerecs.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.entities.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer>{
    public Optional<UserPreference> findByUserIdAndFeatureTypeAndFeatureName(Long userId, FeatureType featureType, String featureName);
    public List<UserPreference> findAllByUserId(Long userId);
}
