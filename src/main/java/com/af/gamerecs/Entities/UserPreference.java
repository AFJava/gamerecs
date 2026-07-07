package com.af.gamerecs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "user_preferences", 
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {
            "user_id",
            "feature_type",
            "feature_value"
        })
    })
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private FeatureType featureType;

    private String featureName;

    private double weight;

    public UserPreference() {

    }

    public UserPreference(User user, FeatureType featureType, String featureName) {
        this.user = user;
        this.featureType = featureType;
        this.featureName = featureName;
        weight = 0.0;
    }

    public UserPreference(User user, FeatureType featureType, String featureName, weight weight) {
        this.user = user;
        this.featureType = featureType;
        this.featureName = featureName;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public String getFeatureName() {
        return featureName;
    }

    public double getWeight() {
        return weight;
    }
}
