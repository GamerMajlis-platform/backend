package com.gamermajilis.model;

public enum EventType {
    TOURNAMENT("Tournament"),
    COMMUNITY_GATHERING("Community Gathering"),
    WORKSHOP("Workshop"),
    MEETUP("Meetup"),
    COMPETITION("Competition"),
    STREAMING_SESSION("Streaming Session"),
    GAMING_SESSION("Gaming Session"),
    ANNOUNCEMENT("Announcement"),
    OTHER("Other");
    
    private final String displayName;
    
    EventType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 