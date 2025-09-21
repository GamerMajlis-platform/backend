package com.gamermajilis.service;

import java.util.Map;

public interface DiscordOAuth2Service {
    
    Map<String, Object> processDiscordCallback(String code);
    
    Map<String, Object> linkDiscordToExistingAccount(Long userId, String code);
    
    Map<String, Object> unlinkDiscordAccount(Long userId);
    
    Map<String, Object> getDiscordUserInfo(Long userId);
    
    Map<String, Object> refreshDiscordToken(Long userId);
}