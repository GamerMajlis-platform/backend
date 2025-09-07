package com.gamermajilis.model;

public enum UserRole {
    REGULAR_GAMER("Regular Gamer"),
    TOURNAMENT_ORGANIZER("Tournament Organizer"),
    SELLER("Seller"),
    ADMINISTRATOR("Administrator"),
    MODERATOR("Moderator");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 