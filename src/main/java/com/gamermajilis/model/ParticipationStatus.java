package com.gamermajilis.model;

public enum ParticipationStatus {
    REGISTERED("Registered"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    ACTIVE("Active"),
    ELIMINATED("Eliminated"),
    WITHDRAWN("Withdrawn"),
    DISQUALIFIED("Disqualified"),
    COMPLETED("Completed");
    
    private final String displayName;
    
    ParticipationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 