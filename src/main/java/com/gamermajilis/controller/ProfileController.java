package com.gamermajilis.controller;

import com.gamermajilis.model.User;
import com.gamermajilis.service.ProfileService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/profile")
@Tag(name = "Profile Management", description = "User profile management endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Get current authenticated user's profile information")
    public ResponseEntity<Map<String, Object>> getMyProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = profileService.getUserProfile(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting user profile", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get profile"));
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID", description = "Get public profile information for a specific user")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        try {
            Map<String, Object> response = profileService.getPublicUserProfile(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user profile for ID: " + userId, e);
            return ResponseEntity.badRequest().body(createErrorResponse("User not found"));
        }
    }

    @PutMapping("/me")
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    public ResponseEntity<Map<String, Object>> updateProfile(
            HttpServletRequest request,
            @RequestParam(required = false) @Size(min = 3, max = 30) String displayName,
            @RequestParam(required = false) @Size(max = 500) String bio,
            @RequestParam(required = false) String gamingPreferences,
            @RequestParam(required = false) String socialLinks,
            @RequestParam(required = false) String privacySettings) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> updateData = new HashMap<>();
            if (displayName != null) updateData.put("displayName", displayName);
            if (bio != null) updateData.put("bio", bio);
            if (gamingPreferences != null) updateData.put("gamingPreferences", gamingPreferences);
            if (socialLinks != null) updateData.put("socialLinks", socialLinks);
            if (privacySettings != null) updateData.put("privacySettings", privacySettings);

            Map<String, Object> response = profileService.updateUserProfile(userId, updateData);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating profile", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update profile"));
        }
    }

    @PostMapping("/me/profile-picture")
    @Operation(summary = "Upload profile picture", description = "Upload and set new profile picture")
    public ResponseEntity<Map<String, Object>> uploadProfilePicture(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("No file provided"));
            }

            // Validate file type and size
            if (!isValidImageFile(file)) {
                return ResponseEntity.badRequest().body(createErrorResponse("Invalid file type. Only JPG, PNG, and GIF files are allowed"));
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return ResponseEntity.badRequest().body(createErrorResponse("File size must not exceed 10MB"));
            }

            Map<String, Object> response = profileService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error uploading profile picture", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to upload profile picture"));
        }
    }

    @DeleteMapping("/me/profile-picture")
    @Operation(summary = "Remove profile picture", description = "Remove current profile picture")
    public ResponseEntity<Map<String, Object>> removeProfilePicture(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = profileService.removeProfilePicture(userId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error removing profile picture", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to remove profile picture"));
        }
    }

    @PostMapping("/me/gaming-stats")
    @Operation(summary = "Update gaming statistics", description = "Update user's gaming statistics")
    public ResponseEntity<Map<String, Object>> updateGamingStats(
            HttpServletRequest request,
            @RequestParam String gamingStatistics) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = profileService.updateGamingStatistics(userId, gamingStatistics);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error updating gaming statistics", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update gaming statistics"));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search user profiles", description = "Search for user profiles by display name or username")
    public ResponseEntity<Map<String, Object>> searchProfiles(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Map<String, Object> response = profileService.searchProfiles(query, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error searching profiles", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Search failed"));
        }
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get profile suggestions", description = "Get suggested user profiles to connect with")
    public ResponseEntity<Map<String, Object>> getProfileSuggestions(
            HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> response = profileService.getProfileSuggestions(userId, limit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error getting profile suggestions", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get suggestions"));
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

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("image/gif")
        );
    }
}
