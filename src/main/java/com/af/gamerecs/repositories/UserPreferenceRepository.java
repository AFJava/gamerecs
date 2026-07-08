package com.af.gamerecs.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Integer>{
    public Optional<UserPreference> findByUserIdAndFeature(Long userId, Feature feature);
    public List<UserPreference> findAllByUserId(Long userId);
}
