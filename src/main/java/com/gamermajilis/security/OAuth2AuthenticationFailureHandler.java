package com.gamermajilis.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        logger.error("Discord OAuth2 authentication failed: {}", exception.getMessage());

        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Authentication failed";

        String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/discord/callback")
                .queryParam("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8))
                .queryParam("success", "false")
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}