package com.af.gamerecs.entities;

public enum FeatureType {
    ESRB_RATING,
    PEGI_RATING,
    FRANCHISE,
    PUBLISHER,
    DEVELOPER,
    SUPPORTING,
    PORTING,
    PLATFORM,
    GENRE,
    THEME,
    GAME_MODE,
    PLAYER_PERSPECTIVE,
    KEYWORD;

    public String toIgdbField() {
        if(isCompany()) {
            return "involved_companies";
        }

        String fieldName = this.toString().toLowerCase() + "s";

        return fieldName;
    }

    public boolean isCompany() {
        return this == FeatureType.PUBLISHER
            || this == FeatureType.DEVELOPER
            || this == FeatureType.SUPPORTING
            || this == FeatureType.PORTING;
    }

    public boolean isAgeRating() {
        return this == FeatureType.ESRB_RATING
            || this == FeatureType.PEGI_RATING;
    }

    public double getWeightMultiplier() {
        return switch(this) {
            case FRANCHISE -> 1.0;
            case PUBLISHER -> 0.4;
            case DEVELOPER -> 0.7;
            case SUPPORTING -> 0.2;
            case PORTING -> 0.1;
            case PLATFORM -> 0.6;
            case GENRE -> 1.5;
            case THEME -> 1.3;
            case GAME_MODE -> 0.6;
            case PLAYER_PERSPECTIVE -> 0.7;
            case KEYWORD -> 0.6;
            case ESRB_RATING -> 0.5;
            case PEGI_RATING -> 0.5;
        };
    }
}
