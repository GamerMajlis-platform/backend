package com.gamermajilis.model;

public enum ProductCondition {
    NEW("New"),
    LIKE_NEW("Like New"),
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor"),
    REFURBISHED("Refurbished"),
    FOR_PARTS("For Parts");
    
    private final String displayName;
    
    ProductCondition(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 