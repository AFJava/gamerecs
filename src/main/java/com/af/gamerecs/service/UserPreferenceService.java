package com.af.gamerecs.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.af.gamerecs.entities.Feature;
import com.af.gamerecs.entities.Game;
import com.af.gamerecs.entities.User;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.UserPreferenceRepository;

@Service
public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public void updatePreference(User user, double rating, Feature feature) {
        UserPreference userPreference = userPreferenceRepository.findByUserIdAndFeature(user.getId(), feature)
            .orElseGet(() -> new UserPreference(
                user,
                feature
            ));

        userPreferenceRepository.save(getNewPreference(userPreference, rating));
    }

    //Update every feature associated with a game in one query
    public void updatePreferenceFromGame(User user, Game game, double rating) {
        Set<Feature> features = game.getFeatures();

        List<UserPreference> currentPreferences = userPreferenceRepository.findAllByUserId(user.getId());

        Map<Feature, UserPreference> featureMap = currentPreferences.stream()
            .collect(Collectors.toMap(
                preference -> new Feature(preference.getFeature().getFeatureType(), preference.getFeature().getIgdbFeatureId(), preference.getFeature().getFeatureName()),
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

    public List<UserPreference> getTopPreferences(User user) {
        List<UserPreference> topPreferences = userPreferenceRepository.findAllByUserId(user.getId()).stream()
            .sorted(Comparator.comparingDouble(UserPreference::getWeight).reversed())
            .limit(10)
            .toList();

        return topPreferences;
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
