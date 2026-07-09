package com.af.gamerecs.entities;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class Feature {
    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false)
    public FeatureType featureType;

    @Column(name = "feature_id", nullable = false)
    public Long igdbFeatureId;

    @Column(name = "feature_name", nullable = false)
    public String featureName;

    public Feature(FeatureType featureType, String featureName) {
        this.featureType = featureType;
        this.featureName = featureName;
    }

    public FeatureType getFeatureType() {
        return featureType;
    }

    public Long getIgdbFeatureId() {
        return igdbFeatureId;
    }

    public String getFeatureName() {
        return featureName;
    }
}
