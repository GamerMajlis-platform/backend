package com.gamermajilis.model;

public enum TournamentStatus {
    DRAFT("Draft"),
    REGISTRATION_OPEN("Registration Open"),
    REGISTRATION_CLOSED("Registration Closed"),
    ACTIVE("Active"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    TournamentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 