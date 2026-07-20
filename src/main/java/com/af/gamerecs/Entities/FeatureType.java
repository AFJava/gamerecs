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
}
