package com.af.gamerecs.entities;

public enum FeatureType {
    FRANCHISE,
    PUBLISHER,
    DEVELOPER,
    INVOLVED_COMPANY,
    PLATFORM,
    GENRE,
    THEME,
    GAME_MODE,
    PLAYER_PERSPECTIVE,
    KEYWORD;

    public String toIgdbField() {
        if(this == FeatureType.PUBLISHER || this == FeatureType.DEVELOPER || this == FeatureType.INVOLVED_COMPANY) {
            return "involved_companies";
        }

        String fieldName = this.toString().toLowerCase() + "s";

        return fieldName;
    }
}
