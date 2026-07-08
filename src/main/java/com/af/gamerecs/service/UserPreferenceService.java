package com.af.gamerecs.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.UserPreferenceRepository;


public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public void updatePreference(User user, double rating, Feature feature) {
        UserPreference userPreference = userPreferenceRepository.findByUserIdAndFeatureTypeAndFeatureName(user.getId(), featureType, featureName)
            .orElseGet(() -> new UserPreference(
                user,
                feature
            ));

        userPreferenceRepository.save(getNewPreference(userPreference, rating));
    }

    //Update every feature associated with a game in one query
    public void updatePreferenceFromGame(User user, Game game, double rating) {
        List<Feature> features = game.getFeatures();

        List<UserPreference> currentPreferences = userPreferenceRepository.findAllByUserId(user.getId());

        Map<Feature, UserPreference> featureMap = currentPreferences.stream()
            .collect(Collectors.toMap(
                preference -> new Feature(preference.getFeature().getFeatureType(), preference.getFeature().getFeatureName()),
                preference -> preference
            ));
        
        List<UserPreference> updatedPreferences = new ArrayList<>();

        for(Feature feature : features) {
            //If feature not in map, create preference; either case, add weight
            UserPreference preference = featureMap.get(feature);

            if(preference == null) {
                preference = new UserPreference(user, feature);
            }

            updatedPreferences.add(getNewPreference(preference, rating));
        }
        
        userPreferenceRepository.saveAll(updatedPreferences);
    }

    public UserPreference getNewPreference(UserPreference preference, double rating) {
        double currentWeight = preference.getWeight();
        
        return new UserPreference(
            preference.getUser(),
            preference.getFeature(),
            currentWeight + rating / 10.0
        );
    }
}
