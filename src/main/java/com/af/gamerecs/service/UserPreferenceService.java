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
import com.af.gamerecs.entities.UserGame;
import com.af.gamerecs.entities.UserPreference;
import com.af.gamerecs.repositories.UserPreferenceRepository;

@Service
public class UserPreferenceService {
    private final UserPreferenceRepository userPreferenceRepository;

    public UserPreferenceService(UserPreferenceRepository userPreferenceRepository) {
        this.userPreferenceRepository = userPreferenceRepository;
    }

    public void updatePreference(User user, double rating, Feature feature) {
        UserPreference preference = userPreferenceRepository.findByUserIdAndFeature(user.getId(), feature)
            .orElseGet(() -> new UserPreference(
                user,
                feature
            ));

        preference.setWeight(preference.getWeight() + rating);

        userPreferenceRepository.save(preference);
    }

    //Update every feature associated with a game in one query
    public void updatePreferenceFromGame(User user, Game game, double rating) {
        List<UserPreference> currentPreferences = userPreferenceRepository.findAllByUserId(user.getId());

        Map<Feature, UserPreference> featureMap = currentPreferences.stream()
            .collect(Collectors.toMap(
                preference -> new Feature(preference.getFeature().getFeatureType(), preference.getFeature().getIgdbFeatureId(), preference.getFeature().getFeatureName()),
                preference -> preference
            ));
        
        List<UserPreference> updatedPreferences = new ArrayList<>();
        
        Set<Feature> features = game.getFeatures();
        
        for(Feature feature : features) {
            //If feature not in map, create preference; either case, add weight
            UserPreference preference = featureMap.get(feature);
            //System.out.println("If preference found, it is " + preference);

            if(preference == null) {
                preference = new UserPreference(user, feature);
                //System.out.println("Preference not found; creating new preference " + preference);
            }

            preference.setWeight(preference.getWeight() + feature.getFeatureType().getWeightMultiplier() * rating);

            updatedPreferences.add(preference);
        }
        
        userPreferenceRepository.saveAll(updatedPreferences);
    }
    public void updatePreferenceFromGame(UserGame userGame) {
        updatePreferenceFromGame(userGame.getUser(), userGame.getGame(), userGame.getRating());
    }

    //Gets all user preferences, sorted most to least preferred
    public List<UserPreference> getSortedPreferences(User user) {
        List<UserPreference> preferences = userPreferenceRepository.findAllByUserId(user.getId()).stream()
            .sorted(Comparator.comparingDouble(UserPreference::getWeight).reversed())
            .toList();

        return preferences;
    }

    public List<Feature> getFeaturesFromPreferences(List<UserPreference> preferences) {
        return preferences.stream()
            .map(UserPreference::getFeature)
            .toList();
    }
}
