package com.gamermajilis.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface ProfileService {
    
    Map<String, Object> getUserProfile(Long userId);
    
    Map<String, Object> getPublicUserProfile(Long userId);
    
    Map<String, Object> updateUserProfile(Long userId, Map<String, Object> updateData);
    
    Map<String, Object> uploadProfilePicture(Long userId, MultipartFile file);
    
    Map<String, Object> removeProfilePicture(Long userId);
    
    Map<String, Object> updateGamingStatistics(Long userId, String gamingStatistics);
    
    Map<String, Object> searchProfiles(String query, int page, int size);
    
    Map<String, Object> getProfileSuggestions(Long userId, int limit);
}
