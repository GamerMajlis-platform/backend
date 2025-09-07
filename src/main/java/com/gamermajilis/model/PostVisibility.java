package com.gamermajilis.model;

public enum PostVisibility {
    PUBLIC("Public"),
    FRIENDS_ONLY("Friends Only"),
    FOLLOWERS_ONLY("Followers Only"),
    PRIVATE("Private"),
    UNLISTED("Unlisted");
    
    private final String displayName;
    
    PostVisibility(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 