package com.gamermajilis.model;

public enum EventLocationType {
    VIRTUAL("Virtual"),
    PHYSICAL("Physical"),
    HYBRID("Hybrid");
    
    private final String displayName;
    
    EventLocationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 