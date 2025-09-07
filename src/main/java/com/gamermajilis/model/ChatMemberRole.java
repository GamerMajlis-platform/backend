package com.gamermajilis.model;

public enum ChatMemberRole {
    MEMBER("Member"),
    MODERATOR("Moderator"),
    ADMIN("Admin");
    
    private final String displayName;
    
    ChatMemberRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 