package com.gamermajilis.model;

public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    FILE("File"),
    AUDIO("Audio"),
    VIDEO("Video"),
    EMOJI("Emoji"),
    SYSTEM("System"),
    ANNOUNCEMENT("Announcement");
    
    private final String displayName;
    
    MessageType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 