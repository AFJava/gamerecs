package com.af.gamerecs.entities;

public enum FeatureType {
    FRANCHISE,
    COMPANY,
    PLATFORM,
    GENRE,
    THEME,
    GAME_MODE,
    PLAYER_PERSPECTIVE,
    KEYWORD;

    public String toIgdbField() {
        if(this == FeatureType.FRANCHISE) {
            return "companies";
        }

        String fieldName = this.toString().toLowerCase() + "s";

        return fieldName;
    }
}
