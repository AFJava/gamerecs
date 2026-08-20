package com.af.gamerecs.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "features",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_features",
            columnNames = {"feature_type", "feature_id"}
        )
    }
)
public class Feature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false)
    public FeatureType featureType;

    @Column(name = "feature_id", nullable = false)
    public Long igdbFeatureId;

    @Column(name = "feature_name", nullable = false)
    public String featureName;

    public Feature() {

    }

    public Feature(FeatureType featureType, Long igdbFeatureId, String featureName) {
        this.featureType = featureType;
        this.igdbFeatureId = igdbFeatureId;
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

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof Feature other)) return false;

        return this.featureType == other.featureType && Objects.equals(igdbFeatureId, other.igdbFeatureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(featureType, igdbFeatureId);
    }

    @Override
    public String toString() {
        return String.format("(%s, %d, %s)", featureType, igdbFeatureId, featureName);
    }
}
