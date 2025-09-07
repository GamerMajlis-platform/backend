package com.gamermajilis.model;

public enum ProductCategory {
    GAMING_CONSOLES("Gaming Consoles"),
    GAMING_ACCESSORIES("Gaming Accessories"),
    PC_COMPONENTS("PC Components"),
    GAMING_PERIPHERALS("Gaming Peripherals"),
    GAMING_CHAIRS("Gaming Chairs"),
    HEADSETS("Headsets"),
    KEYBOARDS("Keyboards"),
    MICE("Mice"),
    MONITORS("Monitors"),
    GAMES("Games"),
    COLLECTIBLES("Collectibles"),
    MERCHANDISE("Merchandise"),
    OTHER("Other");
    
    private final String displayName;
    
    ProductCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 