package com.af.gamerecs.entities;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;

@Embeddable
public class Feature {
    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false)
    public FeatureType featureType;

    @Column(name = "feature_name", nullable = false)
    public String featureName;

    public Feature(FeatureType featureType, String featureName) {
        this.featureType = featureType;
        this.featureName = featureName;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public String getFeatureName() {
        return featureName;
    }
}
