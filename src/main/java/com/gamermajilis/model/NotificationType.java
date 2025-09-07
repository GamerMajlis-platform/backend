package com.gamermajilis.model;

public enum NotificationType {
    // Post-related
    POST_LIKE("Post Liked"),
    POST_COMMENT("Post Comment"),
    POST_SHARE("Post Shared"),
    POST_MENTION("Post Mention"),
    
    // Tournament-related
    TOURNAMENT_INVITATION("Tournament Invitation"),
    TOURNAMENT_REGISTRATION("Tournament Registration"),
    TOURNAMENT_START("Tournament Started"),
    TOURNAMENT_RESULT("Tournament Result"),
    TOURNAMENT_UPDATE("Tournament Update"),
    
    // Event-related
    EVENT_INVITATION("Event Invitation"),
    EVENT_REMINDER("Event Reminder"),
    EVENT_UPDATE("Event Update"),
    EVENT_CANCELLED("Event Cancelled"),
    
    // Chat-related
    CHAT_MESSAGE("Chat Message"),
    CHAT_MENTION("Chat Mention"),
    
    // Marketplace-related
    PRODUCT_SOLD("Product Sold"),
    PRODUCT_REVIEW("Product Review"),
    PRODUCT_INQUIRY("Product Inquiry"),
    
    // Follow/Friend-related
    NEW_FOLLOWER("New Follower"),
    FRIEND_REQUEST("Friend Request"),
    
    // System notifications
    SYSTEM_ANNOUNCEMENT("System Announcement"),
    ACCOUNT_UPDATE("Account Update"),
    SECURITY_ALERT("Security Alert"),
    
    // General
    GENERAL("General Notification");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 