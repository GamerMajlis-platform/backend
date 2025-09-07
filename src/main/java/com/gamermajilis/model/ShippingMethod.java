package com.gamermajilis.model;

public enum ShippingMethod {
    STANDARD("Standard"),
    EXPRESS("Express"),
    OVERNIGHT("Overnight"),
    LOCAL_PICKUP("Local Pickup"),
    DIGITAL_DELIVERY("Digital Delivery");
    
    private final String displayName;
    
    ShippingMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 