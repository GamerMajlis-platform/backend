package com.gamermajilis.model;

public enum ReactionType {
    LIKE("Like"),
    DISLIKE("Dislike"),
    LOVE("Love"),
    LAUGH("Laugh"),
    ANGRY("Angry"),
    SAD("Sad"),
    WOW("Wow");
    
    private final String displayName;
    
    ReactionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 