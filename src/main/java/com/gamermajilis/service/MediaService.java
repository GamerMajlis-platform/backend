package com.gamermajilis.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface MediaService {
    
    Map<String, Object> uploadMedia(Long userId, MultipartFile file, Map<String, Object> metadata);
    
    Map<String, Object> getMediaDetails(Long mediaId);
    
    Map<String, Object> getMediaList(int page, int size, Map<String, Object> filters);
    
    Map<String, Object> updateMedia(Long userId, Long mediaId, Map<String, Object> updateData);
    
    Map<String, Object> deleteMedia(Long userId, Long mediaId);
    
    Map<String, Object> incrementViewCount(Long mediaId);
    
    Map<String, Object> searchMedia(String query, int page, int size, String type);
    
    Map<String, Object> getTrendingMedia(int limit, int days);
    
    String compressMedia(MultipartFile file, String uploadPath) throws Exception;
    
    String generateThumbnail(String videoPath) throws Exception;
}
