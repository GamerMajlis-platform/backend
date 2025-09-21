package com.gamermajilis.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiscordOAuth2ServiceImpl implements DiscordOAuth2Service {
    
    @Override
    public Map<String, Object> processDiscordCallback(String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord OAuth service implementation pending");
        return response;
    }
    
    @Override
    public Map<String, Object> linkDiscordToExistingAccount(Long userId, String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord OAuth service implementation pending");
        return response;
    }
    
    @Override
    public Map<String, Object> unlinkDiscordAccount(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord OAuth service implementation pending");
        return response;
    }
    
    @Override
    public Map<String, Object> getDiscordUserInfo(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord OAuth service implementation pending");
        return response;
    }
    
    @Override
    public Map<String, Object> refreshDiscordToken(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord OAuth service implementation pending");
        return response;
    }
}
