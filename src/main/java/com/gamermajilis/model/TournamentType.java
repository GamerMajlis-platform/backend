package com.gamermajilis.model;

public enum TournamentType {
    SINGLE_ELIMINATION("Single Elimination"),
    DOUBLE_ELIMINATION("Double Elimination"),
    ROUND_ROBIN("Round Robin"),
    SWISS("Swiss System"),
    LADDER("Ladder"),
    CUSTOM("Custom");
    
    private final String displayName;
    
    TournamentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 