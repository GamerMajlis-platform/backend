package com.gamermajilis.model;

public enum AuthProvider {
    EMAIL("Email"),
    DISCORD("Discord");
    
    private final String displayName;
    
    AuthProvider(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 