package com.gamermajilis.security;

import com.gamermajilis.service.DiscordOAuth2Service;
import com.gamermajilis.service.DiscordOAuth2User;
import com.gamermajilis.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        try {
            DiscordOAuth2User oauth2User = (DiscordOAuth2User) authentication.getPrincipal();

            // Generate JWT token
            String token = jwtUtil.generateToken(oauth2User.getUser());

            logger.info("Discord OAuth2 authentication successful for user: {}", oauth2User.getDisplayName());

            // Redirect to frontend with token
            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/discord/callback")
                    .queryParam("token", token)
                    .queryParam("success", "true")
                    .build().toUriString();

            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            logger.error("Error during OAuth2 success handling", e);

            String errorUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/discord/callback")
                    .queryParam("error", "authentication_failed")
                    .queryParam("success", "false")
                    .build().toUriString();

            response.sendRedirect(errorUrl);
        }
    }
}