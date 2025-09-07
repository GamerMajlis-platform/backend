package com.gamermajilis.model;

public enum ChatRoomType {
    DIRECT_MESSAGE("Direct Message"),
    GROUP("Group Chat"),
    TOURNAMENT("Tournament Chat"),
    EVENT("Event Chat"),
    GAME_LOBBY("Game Lobby"),
    GENERAL("General Chat");
    
    private final String displayName;
    
    ChatRoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 