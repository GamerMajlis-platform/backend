package com.gamermajilis.model;

public enum AttendanceStatus {
    INTERESTED("Interested"),
    REGISTERED("Registered"),
    CONFIRMED("Confirmed"),
    CHECKED_IN("Checked In"),
    ATTENDED("Attended"),
    NO_SHOW("No Show"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 