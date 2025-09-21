package com.gamermajilis.controller;

import com.gamermajilis.service.MediaService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/media")
@Tag(name = "Media Management", description = "Media upload and management endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class MediaController {

    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload")
    @Operation(summary = "Upload media file", description = "Upload image or video file with compression")
    public ResponseEntity<Map<String, Object>> uploadMedia(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @RequestParam @Size(min = 1, max = 255) String title,
            @RequestParam(required = false) @Size(max = 1000) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false, defaultValue = "PUBLIC") String visibility) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("No file provided"));
            }

            // Validate file type and size
            if (!isValidMediaFile(file)) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid file type. Only MP4, AVI, MOV, JPG, PNG, GIF files are allowed"));
            }

            // Check file size limits
            if (isVideoFile(file) && file.getSize() > 100 * 1024 * 1024) { // 100MB for videos
                return ResponseEntity.badRequest().body(createErrorResponse("Video file size must not exceed 100MB"));
            } else if (!isVideoFile(file) && file.getSize() > 10 * 1024 * 1024) { // 10MB for images
                return ResponseEntity.badRequest().body(createErrorResponse("Image file size must not exceed 10MB"));
            }

            Map<String, Object> uploadData = new HashMap<>();
            uploadData.put("title", title);
            uploadData.put("description", description);
            uploadData.put("tags", tags);
            uploadData.put("gameCategory", gameCategory);
            uploadData.put("visibility", visibility);

            Map<String, Object> response = mediaService.uploadMedia(userId, file, uploadData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error uploading media", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to upload media"));
        }
    }

    @GetMapping("/{mediaId}")
    @Operation(summary = "Get media details", description = "Get media file information and metadata")
    public ResponseEntity<Map<String, Object>> getMediaDetails(@PathVariable Long mediaId) {
        try {
            Map<String, Object> response = mediaService.getMediaDetails(mediaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting media details for ID: " + mediaId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("Media not found"));
        }
    }

    @GetMapping
    @Operation(summary = "Get media list", description = "Get paginated list of media files")
    public ResponseEntity<Map<String, Object>> getMediaList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false, defaultValue = "false") boolean myMedia) {
        
        try {
            Long userId = null;
            if (myMedia) {
                userId = getUserIdFromRequest(request);
                if (userId == null) {
                    return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
                }
            }

            Map<String, Object> filters = new HashMap<>();
            if (category != null) filters.put("category", category);
            if (type != null) filters.put("type", type);
            if (visibility != null) filters.put("visibility", visibility);
            if (userId != null) filters.put("uploaderId", userId);

            Map<String, Object> response = mediaService.getMediaList(page, size, filters);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting media list", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get media list"));
        }
    }

    @PutMapping("/{mediaId}")
    @Operation(summary = "Update media details", description = "Update media metadata and settings")
    public ResponseEntity<Map<String, Object>> updateMedia(
            HttpServletRequest request,
            @PathVariable Long mediaId,
            @RequestParam(required = false) @Size(min = 1, max = 255) String title,
            @RequestParam(required = false) @Size(max = 1000) String description,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String gameCategory,
            @RequestParam(required = false) String visibility) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> updateData = new HashMap<>();
            if (title != null) updateData.put("title", title);
            if (description != null) updateData.put("description", description);
            if (tags != null) updateData.put("tags", tags);
            if (gameCategory != null) updateData.put("gameCategory", gameCategory);
            if (visibility != null) updateData.put("visibility", visibility);

            Map<String, Object> response = mediaService.updateMedia(userId, mediaId, updateData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating media", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update media"));
        }
    }

    @DeleteMapping("/{mediaId}")
    @Operation(summary = "Delete media file", description = "Delete media file and its metadata")
    public ResponseEntity<Map<String, Object>> deleteMedia(
            HttpServletRequest request,
            @PathVariable Long mediaId) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = mediaService.deleteMedia(userId, mediaId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting media", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete media"));
        }
    }

    @PostMapping("/{mediaId}/view")
    @Operation(summary = "Increment view count", description = "Record a view for the media file")
    public ResponseEntity<Map<String, Object>> incrementViewCount(@PathVariable Long mediaId) {
        try {
            Map<String, Object> response = mediaService.incrementViewCount(mediaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error incrementing view count", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update view count"));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search media files", description = "Search media files by title, tags, or category")
    public ResponseEntity<Map<String, Object>> searchMedia(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        
        try {
            Map<String, Object> response = mediaService.searchMedia(query, page, size, type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching media", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Search failed"));
        }
    }

    @GetMapping("/trending")
    @Operation(summary = "Get trending media", description = "Get trending media files based on views and engagement")
    public ResponseEntity<Map<String, Object>> getTrendingMedia(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "7") int days) {
        
        try {
            Map<String, Object> response = mediaService.getTrendingMedia(limit, days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting trending media", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get trending media"));
        }
    }

    // Helper methods
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("Invalid token in request", e);
            return null;
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    private boolean isValidMediaFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            // Video formats
            contentType.equals("video/mp4") ||
            contentType.equals("video/avi") ||
            contentType.equals("video/quicktime") ||
            // Image formats
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif")
        );
    }

    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("video/");
    }
}
