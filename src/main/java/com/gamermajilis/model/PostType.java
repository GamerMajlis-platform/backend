package com.gamermajilis.model;

public enum PostType {
    TEXT("Text"),
    IMAGE("Image"),
    VIDEO("Video"),
    POLL("Poll"),
    LINK("Link"),
    ANNOUNCEMENT("Announcement"),
    TUTORIAL("Tutorial"),
    REVIEW("Review");
    
    private final String displayName;
    
    PostType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 