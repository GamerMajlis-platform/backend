package com.gamermajilis.model;

public enum ProductStatus {
    DRAFT("Draft"),
    PENDING_REVIEW("Pending Review"),
    ACTIVE("Active"),
    SOLD("Sold"),
    RESERVED("Reserved"),
    SUSPENDED("Suspended"),
    DELETED("Deleted");
    
    private final String displayName;
    
    ProductStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 