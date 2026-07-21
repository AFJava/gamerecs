package com.af.gamerecs.entities;

public enum FeatureType {
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

    public double getWeightMultiplier() {
        return switch(this) {
            case FRANCHISE -> 1.0;
            case PUBLISHER -> 0.5;
            case DEVELOPER -> 0.8;
            case SUPPORTING -> 0.3;
            case PORTING -> 0.1;
            case PLATFORM -> 0.7;
            case GENRE -> 1.2;
            case THEME -> 1.2;
            case GAME_MODE -> 0.8;
            case PLAYER_PERSPECTIVE -> 0.7;
            case KEYWORD -> 0.5;
        };
    }
}
