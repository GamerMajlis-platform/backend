package com.gamermajilis.model;

public enum EventStatus {
    DRAFT("Draft"),
    REGISTRATION_OPEN("Registration Open"),
    REGISTRATION_CLOSED("Registration Closed"),
    ACTIVE("Active"),
    LIVE("Live"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    EventStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 