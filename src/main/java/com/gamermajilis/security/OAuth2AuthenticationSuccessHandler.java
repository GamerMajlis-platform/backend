package com.gamermajilis.security;

import com.gamermajilis.service.DiscordOAuth2User;
import com.gamermajilis.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtUtil jwtUtil;

    // Configure your frontend URL and port here
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.frontend.auth.success-path:/auth/success}")
    private String successPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        try {
            DiscordOAuth2User oauth2User = (DiscordOAuth2User) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtUtil.generateToken(oauth2User.getUser());

            logger.info("Discord OAuth2 authentication successful for user: {} (ID: {})",
                    oauth2User.getDisplayName(), oauth2User.getUserId());

            // Build redirect URL with token
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + successPath)
                    .queryParam("token", token)
                    .queryParam("success", "true")
                    .queryParam("provider", "discord")
                    .build().toUriString();

            logger.info("Redirecting to frontend: {}", redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            logger.error("Error during OAuth2 success handling", e);

            // Redirect to frontend with error
            String errorUrl = UriComponentsBuilder.fromUriString(frontendUrl + successPath)
                    .queryParam("error", "authentication_processing_failed")
                    .queryParam("success", "false")
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}