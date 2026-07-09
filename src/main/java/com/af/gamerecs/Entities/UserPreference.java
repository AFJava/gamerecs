package com.af.gamerecs.entities;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
            "feature_name"
        })
    })
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private Feature feature;

    private double weight;

    public UserPreference() {

    }

    public UserPreference(User user, Feature feature) {
        this.user = user;
        this.feature = feature;
        weight = 0.0;
    }

    public UserPreference(User user, Feature feature, double weight) {
        this.user = user;
        this.feature = feature;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Feature getFeature() {
        return feature;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %s, %f)", id, user.getId(), feature.toString(), weight);
    }
}
