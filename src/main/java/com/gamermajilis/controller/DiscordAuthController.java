package com.gamermajilis.controller;

import com.gamermajilis.service.DiscordOAuth2Service;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth/discord")
@Tag(name = "Discord Authentication", description = "Discord OAuth2 integration endpoints")
@CrossOrigin(origins = "http://localhost:3000")
public class DiscordAuthController {

    private static final Logger logger = LoggerFactory.getLogger(DiscordAuthController.class);

    @Autowired
    private DiscordOAuth2Service discordOAuth2Service;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.discord.client-id}")
    private String discordClientId;

    @Value("${spring.security.oauth2.client.registration.discord.redirect-uri:http://localhost:3000/auth/discord/callback}")
    private String redirectUri;

    @GetMapping("/login")
    @Operation(summary = "Initiate Discord OAuth", description = "Redirect to Discord for OAuth authentication")
    public void discordLogin(HttpServletResponse response) throws IOException {
        String discordAuthUrl = "https://discord.com/api/oauth2/authorize" +
                "?client_id=" + discordClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=identify%20email" +
                "&state=" + generateRandomState();

        logger.info("Redirecting to Discord OAuth: {}", discordAuthUrl);
        response.sendRedirect(discordAuthUrl);
    }

    @GetMapping("/callback")
    @Operation(summary = "Discord OAuth callback", description = "Handle Discord OAuth callback and create user session")
    public ResponseEntity<Map<String, Object>> discordCallback(
            @RequestParam String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpServletResponse response) {

        try {
            if (error != null) {
                logger.warn("Discord OAuth error: {}", error);
                return ResponseEntity.badRequest().body(createErrorResponse("Discord authentication failed: " + error));
            }

            if (code == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authorization code is missing"));
            }

            logger.info("Processing Discord OAuth callback with code: {}", code.substring(0, 8) + "...");

            Map<String, Object> authResult = discordOAuth2Service.processDiscordCallback(code);

            if (!(Boolean) authResult.get("success")) {
                return ResponseEntity.badRequest().body(authResult);
            }

            // Redirect to frontend with token
            String token = (String) authResult.get("token");
            String frontendUrl = "http://localhost:3000/auth/success?token=" + token;

            try {
                response.sendRedirect(frontendUrl);
                return null; // Response handled by redirect
            } catch (IOException e) {
                logger.error("Failed to redirect to frontend", e);
                return ResponseEntity.ok(authResult); // Fallback to JSON response
            }

        } catch (Exception e) {
            logger.error("Error processing Discord OAuth callback", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Authentication failed"));
        }
    }

    @PostMapping("/link")
    @Operation(summary = "Link Discord account", description = "Link Discord account to existing user account")
    public ResponseEntity<Map<String, Object>> linkDiscordAccount(
            HttpServletRequest request,
            @RequestParam String code) {

        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> linkResult = discordOAuth2Service.linkDiscordToExistingAccount(userId, code);
            return ResponseEntity.ok(linkResult);

        } catch (Exception e) {
            logger.error("Error linking Discord account", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to link Discord account"));
        }
    }

    @PostMapping("/unlink")
    @Operation(summary = "Unlink Discord account", description = "Unlink Discord account from user account")
    public ResponseEntity<Map<String, Object>> unlinkDiscordAccount(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> unlinkResult = discordOAuth2Service.unlinkDiscordAccount(userId);
            return ResponseEntity.ok(unlinkResult);

        } catch (Exception e) {
            logger.error("Error unlinking Discord account", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to unlink Discord account"));
        }
    }

    @GetMapping("/user-info")
    @Operation(summary = "Get Discord user info", description = "Get Discord user information for linked account")
    public ResponseEntity<Map<String, Object>> getDiscordUserInfo(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> userInfo = discordOAuth2Service.getDiscordUserInfo(userId);
            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            logger.error("Error getting Discord user info", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get Discord user information"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh Discord token", description = "Refresh Discord OAuth token")
    public ResponseEntity<Map<String, Object>> refreshDiscordToken(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Authentication required"));
            }

            Map<String, Object> refreshResult = discordOAuth2Service.refreshDiscordToken(userId);
            return ResponseEntity.ok(refreshResult);

        } catch (Exception e) {
            logger.error("Error refreshing Discord token", e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to refresh Discord token"));
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

    private String generateRandomState() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
