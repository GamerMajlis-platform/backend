package com.gamermajilis.service;

import com.gamermajilis.model.User;
import com.gamermajilis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProfileServiceImpl implements ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaService mediaService;

    @Value("${app.upload.profile-pictures:uploads/profile-pictures}")
    private String profilePictureUploadDir;

    @Override
    public Map<String, Object> getUserProfile(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();
        Map<String, Object> response = createSuccessResponse("Profile retrieved successfully");
        response.put("user", buildFullUserProfile(user));
        return response;
    }

    @Override
    public Map<String, Object> getPublicUserProfile(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();
        Map<String, Object> response = createSuccessResponse("Profile retrieved successfully");
        response.put("user", buildPublicUserProfile(user));
        return response;
    }

    @Override
    public Map<String, Object> updateUserProfile(Long userId, Map<String, Object> updateData) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();
        boolean hasChanges = false;

        // Update display name
        if (updateData.containsKey("displayName")) {
            String displayName = (String) updateData.get("displayName");
            if (displayName != null && !displayName.equals(user.getDisplayName())) {
                // Check if display name is already taken
                if (userRepository.existsByDisplayNameAndIdNot(displayName, userId)) {
                    return createErrorResponse("Display name already taken");
                }
                user.setDisplayName(displayName);
                hasChanges = true;
            }
        }

        // Update bio
        if (updateData.containsKey("bio")) {
            String bio = (String) updateData.get("bio");
            user.setBio(bio);
            hasChanges = true;
        }

        // Update gaming preferences
        if (updateData.containsKey("gamingPreferences")) {
            String gamingPreferences = (String) updateData.get("gamingPreferences");
            user.setGamingPreferences(gamingPreferences);
            hasChanges = true;
        }

        // Update social links
        if (updateData.containsKey("socialLinks")) {
            String socialLinks = (String) updateData.get("socialLinks");
            user.setSocialLinks(socialLinks);
            hasChanges = true;
        }

        // Update privacy settings
        if (updateData.containsKey("privacySettings")) {
            String privacySettings = (String) updateData.get("privacySettings");
            user.setPrivacySettings(privacySettings);
            hasChanges = true;
        }

        if (hasChanges) {
            userRepository.save(user);
            logger.info("Profile updated for user ID: {}", userId);
        }

        Map<String, Object> response = createSuccessResponse("Profile updated successfully");
        response.put("user", buildFullUserProfile(user));
        return response;
    }

    @Override
    public Map<String, Object> uploadProfilePicture(Long userId, MultipartFile file) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(profilePictureUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            String newFilename = "profile_" + userId + "_" + System.currentTimeMillis() + fileExtension;
            Path filePath = uploadPath.resolve(newFilename);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Remove old profile picture if exists
            if (user.getProfilePictureUrl() != null) {
                deleteOldProfilePicture(user.getProfilePictureUrl());
            }

            // Update user profile picture URL
            String profilePictureUrl = "/uploads/profile-pictures/" + newFilename;
            user.setProfilePictureUrl(profilePictureUrl);
            userRepository.save(user);

            logger.info("Profile picture uploaded for user ID: {}", userId);

            Map<String, Object> response = createSuccessResponse("Profile picture uploaded successfully");
            response.put("profilePictureUrl", profilePictureUrl);
            return response;

        } catch (IOException e) {
            logger.error("Error uploading profile picture for user ID: " + userId, e);
            return createErrorResponse("Failed to upload profile picture");
        }
    }

    @Override
    public Map<String, Object> removeProfilePicture(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();
        
        if (user.getProfilePictureUrl() != null) {
            deleteOldProfilePicture(user.getProfilePictureUrl());
            user.setProfilePictureUrl(null);
            userRepository.save(user);
            logger.info("Profile picture removed for user ID: {}", userId);
        }

        return createSuccessResponse("Profile picture removed successfully");
    }

    @Override
    public Map<String, Object> updateGamingStatistics(Long userId, String gamingStatistics) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return createErrorResponse("User not found");
        }

        User user = userOpt.get();
        user.setGamingStatistics(gamingStatistics);
        userRepository.save(user);

        logger.info("Gaming statistics updated for user ID: {}", userId);

        Map<String, Object> response = createSuccessResponse("Gaming statistics updated successfully");
        response.put("gamingStatistics", gamingStatistics);
        return response;
    }

    @Override
    public Map<String, Object> searchProfiles(String query, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findByDisplayNameContainingIgnoreCaseOrDiscordUsernameContainingIgnoreCase(
                query, query, pageable);

            List<Map<String, Object>> profiles = userPage.getContent().stream()
                .filter(user -> user.getActive() && !user.getBanned())
                .map(this::buildPublicUserProfile)
                .collect(Collectors.toList());

            Map<String, Object> response = createSuccessResponse("Profiles found");
            response.put("profiles", profiles);
            response.put("totalElements", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);

            return response;

        } catch (Exception e) {
            logger.error("Error searching profiles with query: " + query, e);
            return createErrorResponse("Search failed");
        }
    }

    @Override
    public Map<String, Object> getProfileSuggestions(Long userId, int limit) {
        try {
            // Simple suggestion logic: get recently active users with similar gaming preferences
            List<User> recentUsers = userRepository.findActiveUsersExcluding(userId, PageRequest.of(0, limit * 2));
            
            List<Map<String, Object>> suggestions = recentUsers.stream()
                .limit(limit)
                .map(this::buildPublicUserProfile)
                .collect(Collectors.toList());

            Map<String, Object> response = createSuccessResponse("Profile suggestions retrieved");
            response.put("suggestions", suggestions);
            return response;

        } catch (Exception e) {
            logger.error("Error getting profile suggestions for user ID: " + userId, e);
            return createErrorResponse("Failed to get suggestions");
        }
    }

    // Helper methods
    private Map<String, Object> buildFullUserProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("displayName", user.getDisplayName());
        profile.put("email", user.getEmail());
        profile.put("bio", user.getBio());
        profile.put("profilePictureUrl", user.getProfilePictureUrl());
        profile.put("gamingPreferences", user.getGamingPreferences());
        profile.put("socialLinks", user.getSocialLinks());
        profile.put("gamingStatistics", user.getGamingStatistics());
        profile.put("roles", user.getRoles());
        profile.put("discordUsername", user.getDiscordUsername());
        profile.put("lastLogin", user.getLastLogin());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("privacySettings", user.getPrivacySettings());
        profile.put("emailVerified", user.getEmailVerified());
        return profile;
    }

    private Map<String, Object> buildPublicUserProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("displayName", user.getDisplayName());
        profile.put("bio", user.getBio());
        profile.put("profilePictureUrl", user.getProfilePictureUrl());
        profile.put("gamingPreferences", user.getGamingPreferences());
        profile.put("socialLinks", user.getSocialLinks());
        profile.put("gamingStatistics", user.getGamingStatistics());
        profile.put("roles", user.getRoles());
        profile.put("discordUsername", user.getDiscordUsername());
        profile.put("createdAt", user.getCreatedAt());
        return profile;
    }

    private void deleteOldProfilePicture(String profilePictureUrl) {
        try {
            if (profilePictureUrl.startsWith("/uploads/profile-pictures/")) {
                String filename = profilePictureUrl.substring(profilePictureUrl.lastIndexOf('/') + 1);
                Path filePath = Paths.get(profilePictureUploadDir).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            logger.warn("Failed to delete old profile picture: " + profilePictureUrl, e);
        }
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
