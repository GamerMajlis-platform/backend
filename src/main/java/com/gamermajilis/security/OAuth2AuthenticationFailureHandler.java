package com.gamermajilis.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    // Configure your frontend URL and port here
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.frontend.auth.failure-path:/auth/failure}")
    private String failurePath;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        logger.error("Discord OAuth2 authentication failed: {}", exception.getMessage());

        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Authentication failed";

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + failurePath)
                .queryParam("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8))
                .queryParam("success", "false")
                .queryParam("provider", "discord")
                .build().toUriString();

        logger.info("Redirecting to frontend error page: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}