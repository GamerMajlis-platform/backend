package com.gamermajilis.model;

public enum MediaVisibility {
    PUBLIC("Public"),
    UNLISTED("Unlisted"),
    FRIENDS_ONLY("Friends Only"),
    PRIVATE("Private");
    
    private final String displayName;
    
    MediaVisibility(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 