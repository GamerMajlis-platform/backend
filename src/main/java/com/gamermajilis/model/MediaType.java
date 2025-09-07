package com.gamermajilis.model;

public enum MediaType {
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DOCUMENT("Document");
    
    private final String displayName;
    
    MediaType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static MediaType fromContentType(String contentType) {
        if (contentType == null) {
            return DOCUMENT;
        }
        
        if (contentType.startsWith("image/")) {
            return IMAGE;
        } else if (contentType.startsWith("video/")) {
            return VIDEO;
        } else if (contentType.startsWith("audio/")) {
            return AUDIO;
        } else {
            return DOCUMENT;
        }
    }
} 