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
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class DiscordOAuth2ServiceImpl extends DefaultOAuth2UserService implements DiscordOAuth2Service {

    private static final Logger logger = LoggerFactory.getLogger(DiscordOAuth2ServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception e) {
            logger.error("Error processing Discord OAuth2 user", e);
            throw new OAuth2AuthenticationException("Processing failed: " + e.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        String discordId = String.valueOf(attributes.get("id"));
        String discordUsername = (String) attributes.get("username");
        String email = (String) attributes.get("email");
        String discriminator = (String) attributes.get("discriminator");
        String avatar = (String) attributes.get("avatar");
        Boolean verified = (Boolean) attributes.get("verified");

        logger.info("Processing Discord user - ID: {}, Username: {}, Email: {}",
                discordId, discordUsername, email);

        User user = findOrCreateUser(discordId, discordUsername, email, discriminator, avatar, verified);

        return new DiscordOAuth2User(attributes, user);
    }

    private User findOrCreateUser(String discordId, String discordUsername, String email,
            String discriminator, String avatar, Boolean verified) {
        // Check if user already exists by Discord ID
        Optional<User> existingUser = userRepository.findByDiscordId(discordId);

        if (existingUser.isPresent()) {
            // Update existing user's information
            User user = existingUser.get();
            updateDiscordUserInfo(user, discordUsername, email, discriminator, avatar);
            user.setLastLogin(LocalDateTime.now());
            return userRepository.save(user);
        }

        // Check if user exists by email (in case they signed up with email first)
        if (email != null && !email.isEmpty()) {
            Optional<User> emailUser = userRepository.findByEmail(email);
            if (emailUser.isPresent()) {
                // Link Discord account to existing email user
                User user = emailUser.get();
                linkDiscordToUser(user, discordId, discordUsername, discriminator, avatar);
                user.setLastLogin(LocalDateTime.now());
                return userRepository.save(user);
            }
        }

        // Create new user
        return createNewDiscordUser(discordId, discordUsername, email, discriminator, avatar, verified);
    }

    private User createNewDiscordUser(String discordId, String discordUsername, String email,
            String discriminator, String avatar, Boolean verified) {
        User user = new User();
        user.setDiscordId(discordId);
        user.setDiscordUsername(discordUsername);
        user.setDiscordDiscriminator(discriminator);
        user.setEmail(email);
        user.setDisplayName(discordUsername); // Use Discord username as display name initially
        user.setAuthProvider(AuthProvider.DISCORD);
        user.setEmailVerified(verified != null ? verified : true); // Discord emails are usually verified
        user.setActive(true);
        user.setLastLogin(LocalDateTime.now());

        if (avatar != null) {
            user.setProfilePictureUrl("https://cdn.discordapp.com/avatars/" + discordId + "/" + avatar + ".png");
        }

        // Check for unique display name
        String originalDisplayName = discordUsername;
        int counter = 1;
        while (userRepository.existsByDisplayName(user.getDisplayName())) {
            user.setDisplayName(originalDisplayName + counter);
            counter++;
        }

        logger.info("Creating new Discord user: {} with display name: {}", discordUsername, user.getDisplayName());
        return userRepository.save(user);
    }

    private void linkDiscordToUser(User user, String discordId, String discordUsername,
            String discriminator, String avatar) {
        user.setDiscordId(discordId);
        user.setDiscordUsername(discordUsername);
        user.setDiscordDiscriminator(discriminator);

        if (avatar != null) {
            user.setProfilePictureUrl("https://cdn.discordapp.com/avatars/" + discordId + "/" + avatar + ".png");
        }

        logger.info("Linked Discord account {} to existing user {}", discordUsername, user.getEmail());
    }

    private void updateDiscordUserInfo(User user, String discordUsername, String email,
            String discriminator, String avatar) {
        boolean updated = false;

        if (!Objects.equals(user.getDiscordUsername(), discordUsername)) {
            user.setDiscordUsername(discordUsername);
            updated = true;
        }

        if (!Objects.equals(user.getDiscordDiscriminator(), discriminator)) {
            user.setDiscordDiscriminator(discriminator);
            updated = true;
        }

        if (email != null && !Objects.equals(user.getEmail(), email)) {
            // Only update email if current email is empty
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                user.setEmail(email);
                updated = true;
            }
        }

        String newAvatarUrl = avatar != null
                ? "https://cdn.discordapp.com/avatars/" + user.getDiscordId() + "/" + avatar + ".png"
                : null;

        if (!Objects.equals(user.getProfilePictureUrl(), newAvatarUrl)) {
            user.setProfilePictureUrl(newAvatarUrl);
            updated = true;
        }

        if (updated) {
            logger.info("Updated Discord user info for: {}", discordUsername);
        }
    }

    @Override
    public Map<String, Object> processDiscordCallback(String code) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Exchange code for access token
            Map<String, Object> tokenResponse = exchangeCodeForToken(code);

            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                response.put("success", false);
                response.put("message", "Failed to exchange code for access token");
                return response;
            }

            String accessToken = (String) tokenResponse.get("access_token");

            // Get user info from Discord
            Map<String, Object> userInfo = getDiscordUserInfo(accessToken);

            if (userInfo == null) {
                response.put("success", false);
                response.put("message", "Failed to get user information from Discord");
                return response;
            }

            // Process user
            String discordId = String.valueOf(userInfo.get("id"));
            String discordUsername = (String) userInfo.get("username");
            String email = (String) userInfo.get("email");
            String discriminator = (String) userInfo.get("discriminator");
            String avatar = (String) userInfo.get("avatar");
            Boolean verified = (Boolean) userInfo.get("verified");

            User user = findOrCreateUser(discordId, discordUsername, email, discriminator, avatar, verified);

            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(user);

            response.put("success", true);
            response.put("message", "Discord authentication successful");
            response.put("token", jwtToken);
            response.put("user", createUserResponse(user));

        } catch (Exception e) {
            logger.error("Error processing Discord callback", e);
            response.put("success", false);
            response.put("message", "Discord authentication failed: " + e.getMessage());
        }

        return response;
    }

    private Map<String, Object> exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String requestBody = "client_id=1416218898063429724" +
                    "&client_secret=AiAhe95Q2fOoDdi_ZGVr6PNUS3s6rKXo" +
                    "&grant_type=authorization_code" +
                    "&code=" + code +
                    "&redirect_uri=http://localhost:3000/auth/discord/callback";

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://discord.com/api/oauth2/token", request, Map.class);

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error exchanging code for token", e);
            return null;
        }
    }

    private Map<String, Object> getDiscordUserInfo(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://discord.com/api/users/@me", HttpMethod.GET, request, Map.class);

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error getting Discord user info", e);
            return null;
        }
    }

    @Override
    public Map<String, Object> linkDiscordToExistingAccount(Long userId, String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Link Discord account feature will be implemented later");
        return response;
    }

    @Override
    public Map<String, Object> unlinkDiscordAccount(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Unlink Discord account feature will be implemented later");
        return response;
    }

    @Override
    public Map<String, Object> getDiscordUserInfo(Long userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "User not found");
                return response;
            }

            User user = userOpt.get();

            if (user.getDiscordId() == null) {
                response.put("success", false);
                response.put("message", "User does not have Discord account linked");
                return response;
            }

            Map<String, Object> discordInfo = new HashMap<>();
            discordInfo.put("discordId", user.getDiscordId());
            discordInfo.put("discordUsername", user.getDiscordUsername());
            discordInfo.put("discordDiscriminator", user.getDiscordDiscriminator());
            discordInfo.put("profilePictureUrl", user.getProfilePictureUrl());

            response.put("success", true);
            response.put("discordInfo", discordInfo);

        } catch (Exception e) {
            logger.error("Error getting Discord user info", e);
            response.put("success", false);
            response.put("message", "Failed to get Discord user information");
        }

        return response;
    }

    @Override
    public Map<String, Object> refreshDiscordToken(Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Discord token refresh feature will be implemented later");
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