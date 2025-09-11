package com.gamermajilis.service;

import com.gamermajilis.model.AuthProvider;
import com.gamermajilis.model.User;
import com.gamermajilis.repository.UserRepository;
import com.gamermajilis.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DiscordOAuth2Service extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(DiscordOAuth2Service.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            logger.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String discordId = oauth2User.getAttribute("id");
        String email = oauth2User.getAttribute("email");
        String username = oauth2User.getAttribute("username");
        String discriminator = oauth2User.getAttribute("discriminator");
        String avatar = oauth2User.getAttribute("avatar");

        if (discordId == null) {
            throw new OAuth2AuthenticationException("Discord ID not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByDiscordId(discordId);
        User user;

        if (userOptional.isPresent()) {
            // Update existing user
            user = userOptional.get();
            user = updateExistingUser(user, email, username, discriminator, avatar);
        } else {
            // Check if user with same email exists
            if (email != null && userRepository.existsByEmail(email)) {
                throw new OAuth2AuthenticationException("Email already registered with different account");
            }

            // Create new user
            user = createNewUser(discordId, email, username, discriminator, avatar);
        }

        return new DiscordOAuth2User(oauth2User.getAttributes(), user);
    }

    private User updateExistingUser(User user, String email, String username, String discriminator, String avatar) {
        // Update Discord info
        user.setDiscordUsername(username);
        user.setDiscordDiscriminator(discriminator);
        user.setLastLogin(LocalDateTime.now());

        // Update profile picture if avatar exists
        if (avatar != null) {
            String avatarUrl = "https://cdn.discordapp.com/avatars/" + user.getDiscordId() + "/" + avatar + ".png";
            user.setProfilePictureUrl(avatarUrl);
        }

        // Update email if provided and different
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            user.setEmailVerified(true); // Discord emails are considered verified
        }

        return userRepository.save(user);
    }

    private User createNewUser(String discordId, String email, String username, String discriminator, String avatar) {
        // Generate display name
        String displayName = username;
        int counter = 1;
        while (userRepository.existsByDisplayName(displayName)) {
            displayName = username + counter;
            counter++;
        }

        User user = new User();
        user.setDiscordId(discordId);
        user.setDiscordUsername(username);
        user.setDiscordDiscriminator(discriminator);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setAuthProvider(AuthProvider.DISCORD);
        user.setEmailVerified(true); // Discord emails are considered verified
        user.setActive(true);

        // Set profile picture
        if (avatar != null) {
            String avatarUrl = "https://cdn.discordapp.com/avatars/" + discordId + "/" + avatar + ".png";
            user.setProfilePictureUrl(avatarUrl);
        }

        User savedUser = userRepository.save(user);

        // Send welcome email
        if (email != null) {
            try {
                emailService.sendWelcomeEmail(email, displayName);
            } catch (Exception e) {
                logger.error("Failed to send welcome email to: {}", email, e);
            }
        }

        logger.info("New Discord user created: {}", displayName);
        return savedUser;
    }

    public Map<String, Object> handleDiscordLogin(User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Generate JWT token
            String token = jwtUtil.generateToken(user);

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            response.put("success", true);
            response.put("message", "Discord login successful");
            response.put("token", token);
            response.put("user", createUserResponse(user));

        } catch (Exception e) {
            logger.error("Error during Discord login", e);
            response.put("success", false);
            response.put("message", "An error occurred during Discord login");
        }

        return response;
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("displayName", user.getDisplayName());
        userResponse.put("bio", user.getBio());
        userResponse.put("profilePictureUrl", user.getProfilePictureUrl());
        userResponse.put("roles", user.getRoles());
        userResponse.put("authProvider", user.getAuthProvider());
        userResponse.put("emailVerified", user.getEmailVerified());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("lastLogin", user.getLastLogin());
        userResponse.put("discordId", user.getDiscordId());
        userResponse.put("discordUsername", user.getDiscordUsername());
        return userResponse;
    }
}