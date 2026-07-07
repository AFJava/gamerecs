package com.af.gamerecs.service;

import com.af.gamerecs.entities.FeatureType;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.UserPreferenceRepository;


public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public void updatePreference(double rating, User user, FeatureType featureType, String featureName) {
        UserPreference userPreference = userPreferenceRepository.findByUserIdAndFeatureTypeAndFeatureName(user.getId(), featureType, featureName)
            .orElseGet(() -> new UserPreference(
                user,
                featureType,
                featureName
            ));
        
        double currentWeight = userPreference.getWeight();
        
        UserPreference newUserPreference = new UserPreference(
            user,
            featureType,
            featureName,
            currentWeight + rating / 10.0
        );

        userPreferenceRepository.save(newUserPreference);
    }
}
