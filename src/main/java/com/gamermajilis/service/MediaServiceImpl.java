package com.gamermajilis.service;

import com.gamermajilis.model.*;
import com.gamermajilis.repository.MediaRepository;
import com.gamermajilis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MediaServiceImpl implements MediaService {
    
    private static final Logger logger = LoggerFactory.getLogger(MediaServiceImpl.class);
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private final String uploadDirectory = "/tmp/uploads/media/";
    private final String thumbnailDirectory = "/tmp/uploads/media/thumbnails/";
    
    @Override
    public Map<String, Object> uploadMedia(Long userId, MultipartFile file, Map<String, Object> uploadData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }
            
            User uploader = userOpt.get();
            
            // Create upload directories if they don't exist
            createDirectoriesIfNeeded();
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String storedFilename = System.currentTimeMillis() + "_" + userId + fileExtension;
            String filePath = uploadDirectory + storedFilename;
            
            // Save file to disk
            Path uploadPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Determine media type
            MediaType mediaType = MediaType.fromContentType(file.getContentType());
            
            // Create Media entity
            Media media = new Media();
            media.setTitle((String) uploadData.get("title"));
            media.setDescription((String) uploadData.get("description"));
            media.setOriginalFilename(originalFilename);
            media.setStoredFilename(storedFilename);
            media.setFilePath(filePath);
            media.setContentType(file.getContentType());
            media.setMediaType(mediaType);
            media.setFileSize(file.getSize());
            media.setUploader(uploader);
            
            // Set optional fields
            String tags = (String) uploadData.get("tags");
            if (tags != null && !tags.trim().isEmpty()) {
                media.setTags(tags);
            }
            
            String gameCategory = (String) uploadData.get("gameCategory");
            if (gameCategory != null && !gameCategory.trim().isEmpty()) {
                media.setGameCategory(gameCategory);
            }
            
            String visibilityStr = (String) uploadData.get("visibility");
            if (visibilityStr != null) {
                try {
                    MediaVisibility visibility = MediaVisibility.valueOf(visibilityStr.toUpperCase());
                    media.setVisibility(visibility);
                } catch (IllegalArgumentException e) {
                    media.setVisibility(MediaVisibility.PUBLIC);
                }
            } else {
                media.setVisibility(MediaVisibility.PUBLIC);
            }
            
            // Set default moderation status
            media.setModerationStatus("APPROVED"); // Auto-approve for now
            
            // Handle compression and thumbnails for videos (simplified implementation)
            if (media.isVideo()) {
                // In a real implementation, you'd use FFmpeg here
                media.setCompressedSize(file.getSize()); // Same size for now
                media.setCompressionRatio(1.0);
                media.setResolution("1920x1080"); // Default resolution
                media.setDuration(30L); // Default duration in seconds
                
                // Generate thumbnail (simplified)
                String thumbnailPath = generateThumbnail(filePath);
                media.setThumbnailPath(thumbnailPath);
            } else if (media.isImage()) {
                // Set default image dimensions
                media.setWidth(1920);
                media.setHeight(1080);
            }
            
            // Save to database
            Media savedMedia = mediaRepository.save(media);
            
            // Prepare response
            response.put("success", true);
            response.put("message", "Media uploaded successfully");
            response.put("media", formatMediaForResponse(savedMedia));
            
        } catch (Exception e) {
            logger.error("Error uploading media", e);
            response.put("success", false);
            response.put("message", "Failed to upload media: " + e.getMessage());
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getMediaDetails(Long mediaId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Media> mediaOpt = mediaRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                mediaId, MediaVisibility.PUBLIC, "APPROVED");
            
            if (mediaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Media not found");
                return response;
            }
            
            Media media = mediaOpt.get();
            
            response.put("success", true);
            response.put("message", "Media retrieved successfully");
            response.put("media", formatMediaDetailsForResponse(media));
            
        } catch (Exception e) {
            logger.error("Error getting media details", e);
            response.put("success", false);
            response.put("message", "Failed to get media details");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getMediaList(int page, int size, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Media> mediaPage;
            
            Long uploaderId = (Long) filters.get("uploaderId");
            String category = (String) filters.get("category");
            String type = (String) filters.get("type");
            String visibility = (String) filters.get("visibility");
            
            if (uploaderId != null) {
                // Get user's media
                mediaPage = mediaRepository.findByUploaderIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                    uploaderId, pageable);
            } else {
                // Get public approved media with filters
                MediaVisibility mediaVisibility = MediaVisibility.PUBLIC;
                String moderationStatus = "APPROVED";
                
                if (category != null && type != null) {
                    MediaType mediaType = MediaType.valueOf(type.toUpperCase());
                    mediaPage = mediaRepository.findByMediaTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        mediaType, mediaVisibility, moderationStatus, pageable);
                } else if (category != null) {
                    mediaPage = mediaRepository.findByGameCategoryAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        category, mediaVisibility, moderationStatus, pageable);
                } else if (type != null) {
                    MediaType mediaType = MediaType.valueOf(type.toUpperCase());
                    mediaPage = mediaRepository.findByMediaTypeAndVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        mediaType, mediaVisibility, moderationStatus, pageable);
                } else {
                    mediaPage = mediaRepository.findByVisibilityAndModerationStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        mediaVisibility, moderationStatus, pageable);
                }
            }
            
            List<Map<String, Object>> mediaList = mediaPage.getContent().stream()
                .map(this::formatMediaForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Media list retrieved");
            response.put("media", mediaList);
            response.put("totalElements", mediaPage.getTotalElements());
            response.put("totalPages", mediaPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error getting media list", e);
            response.put("success", false);
            response.put("message", "Failed to get media list");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> updateMedia(Long userId, Long mediaId, Map<String, Object> updateData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Media> mediaOpt = mediaRepository.findByIdAndUploaderIdAndDeletedAtIsNull(mediaId, userId);
            
            if (mediaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Media not found or access denied");
                return response;
            }
            
            Media media = mediaOpt.get();
            
            // Update fields if provided
            if (updateData.containsKey("title")) {
                media.setTitle((String) updateData.get("title"));
            }
            if (updateData.containsKey("description")) {
                media.setDescription((String) updateData.get("description"));
            }
            if (updateData.containsKey("tags")) {
                media.setTags((String) updateData.get("tags"));
            }
            if (updateData.containsKey("gameCategory")) {
                media.setGameCategory((String) updateData.get("gameCategory"));
            }
            if (updateData.containsKey("visibility")) {
                String visibilityStr = (String) updateData.get("visibility");
                try {
                    MediaVisibility visibility = MediaVisibility.valueOf(visibilityStr.toUpperCase());
                    media.setVisibility(visibility);
                } catch (IllegalArgumentException e) {
                    // Keep current visibility if invalid
                }
            }
            
            Media updatedMedia = mediaRepository.save(media);
            
            response.put("success", true);
            response.put("message", "Media updated successfully");
            response.put("media", formatMediaForResponse(updatedMedia));
            
        } catch (Exception e) {
            logger.error("Error updating media", e);
            response.put("success", false);
            response.put("message", "Failed to update media");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> deleteMedia(Long userId, Long mediaId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Media> mediaOpt = mediaRepository.findByIdAndUploaderIdAndDeletedAtIsNull(mediaId, userId);
            
            if (mediaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Media not found or access denied");
                return response;
            }
            
            Media media = mediaOpt.get();
            
            // Soft delete
            media.setDeletedAt(LocalDateTime.now());
            mediaRepository.save(media);
            
            // TODO: In production, also delete physical file after some delay
            
            response.put("success", true);
            response.put("message", "Media deleted successfully");
            
        } catch (Exception e) {
            logger.error("Error deleting media", e);
            response.put("success", false);
            response.put("message", "Failed to delete media");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> incrementViewCount(Long mediaId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Media> mediaOpt = mediaRepository.findByIdAndVisibilityAndModerationStatusAndDeletedAtIsNull(
                mediaId, MediaVisibility.PUBLIC, "APPROVED");
            
            if (mediaOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Media not found");
                return response;
            }
            
            Media media = mediaOpt.get();
            media.incrementViewCount();
            mediaRepository.save(media);
            
            response.put("success", true);
            response.put("message", "View count updated");
            response.put("newViewCount", media.getViewCount());
            
        } catch (Exception e) {
            logger.error("Error incrementing view count", e);
            response.put("success", false);
            response.put("message", "Failed to update view count");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> searchMedia(String query, int page, int size, String type) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Media> mediaPage;
            
            if (type != null && !type.trim().isEmpty()) {
                MediaType mediaType = MediaType.valueOf(type.toUpperCase());
                mediaPage = mediaRepository.searchMediaByType(
                    query, mediaType, MediaVisibility.PUBLIC, pageable);
            } else {
                mediaPage = mediaRepository.searchMedia(
                    query, MediaVisibility.PUBLIC, pageable);
            }
            
            List<Map<String, Object>> mediaList = mediaPage.getContent().stream()
                .map(this::formatMediaForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Media search completed");
            response.put("media", mediaList);
            response.put("totalElements", mediaPage.getTotalElements());
            response.put("totalPages", mediaPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);
            
        } catch (Exception e) {
            logger.error("Error searching media", e);
            response.put("success", false);
            response.put("message", "Search failed");
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getTrendingMedia(int limit, int days) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            Pageable pageable = PageRequest.of(0, limit);
            
            List<Media> trendingMedia = mediaRepository.findTrendingMedia(since, pageable);
            
            List<Map<String, Object>> mediaList = trendingMedia.stream()
                .map(this::formatMediaForResponse)
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Trending media retrieved");
            response.put("media", mediaList);
            
        } catch (Exception e) {
            logger.error("Error getting trending media", e);
            response.put("success", false);
            response.put("message", "Failed to get trending media");
        }
        
        return response;
    }
    
    @Override
    public String compressMedia(MultipartFile file, String uploadPath) throws Exception {
        // Simplified implementation - in production use FFmpeg
        return "compressed_" + file.getOriginalFilename();
    }
    
    @Override
    public String generateThumbnail(String videoPath) throws Exception {
        // Simplified implementation - in production use FFmpeg
        String thumbnailFilename = "thumb_" + System.currentTimeMillis() + ".jpg";
        String thumbnailPath = thumbnailDirectory + thumbnailFilename;
        
        // Create a placeholder thumbnail file
        createDirectoriesIfNeeded();
        Files.write(Paths.get(thumbnailPath), "placeholder thumbnail".getBytes());
        
        return thumbnailPath;
    }
    
    // Helper methods
    private void createDirectoriesIfNeeded() throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDirectory);
            Path thumbnailPath = Paths.get(thumbnailDirectory);
            
            logger.info("Creating upload directories: {} and {}", uploadPath.toString(), thumbnailPath.toString());
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath.toString());
            }
            if (!Files.exists(thumbnailPath)) {
                Files.createDirectories(thumbnailPath);
                logger.info("Created thumbnail directory: {}", thumbnailPath.toString());
            }
            
            // Verify directories are writable
            if (!Files.isWritable(uploadPath)) {
                throw new IOException("Upload directory is not writable: " + uploadPath.toString());
            }
            if (!Files.isWritable(thumbnailPath)) {
                throw new IOException("Thumbnail directory is not writable: " + thumbnailPath.toString());
            }
            
        } catch (IOException e) {
            logger.error("Failed to create upload directories", e);
            throw e;
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
    
    private Map<String, Object> formatMediaForResponse(Media media) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", media.getId());
        response.put("title", media.getTitle());
        response.put("description", media.getDescription());
        response.put("originalFilename", media.getOriginalFilename());
        response.put("storedFilename", media.getStoredFilename());
        response.put("filePath", media.getFilePath());
        response.put("mediaType", media.getMediaType());
        response.put("fileSize", media.getFileSize());
        response.put("compressedSize", media.getCompressedSize());
        response.put("compressionRatio", media.getCompressionRatio());
        response.put("thumbnailPath", media.getThumbnailPath());
        response.put("duration", media.getDuration());
        response.put("resolution", media.getResolution());
        response.put("tags", media.getTags());
        response.put("gameCategory", media.getGameCategory());
        response.put("visibility", media.getVisibility());
        response.put("viewCount", media.getViewCount());
        response.put("downloadCount", media.getDownloadCount());
        response.put("createdAt", media.getCreatedAt());
        
        // Include uploader info
        if (media.getUploader() != null) {
            Map<String, Object> uploaderInfo = new HashMap<>();
            uploaderInfo.put("id", media.getUploader().getId());
            uploaderInfo.put("displayName", media.getUploader().getDisplayName());
            uploaderInfo.put("profilePictureUrl", media.getUploader().getProfilePictureUrl());
            response.put("uploader", uploaderInfo);
        }
        
        return response;
    }
    
    private Map<String, Object> formatMediaDetailsForResponse(Media media) {
        Map<String, Object> response = formatMediaForResponse(media);
        // Add additional details for full media view
        response.put("moderationStatus", media.getModerationStatus());
        response.put("width", media.getWidth());
        response.put("height", media.getHeight());
        response.put("updatedAt", media.getUpdatedAt());
        return response;
    }
}
