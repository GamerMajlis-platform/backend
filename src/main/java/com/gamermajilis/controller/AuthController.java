package com.gamermajilis.controller;

import com.gamermajilis.service.AuthService;
import com.gamermajilis.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for signup, login, and logout")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account with email verification")
    public ResponseEntity<Map<String, Object>> signup(
            @RequestParam @Email @NotBlank String email,
            @RequestParam @NotBlank @Size(min = 6) String password,
            @RequestParam @NotBlank @Size(min = 3, max = 30) String displayName) {

        logger.info("Signup attempt for email: {}", email);

        Map<String, Object> response = authService.signup(email, password, displayName);

        if ((Boolean) response.get("success")) {
            logger.info("User successfully registered: {}", email);
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Signup failed for email: {} - {}", email, response.get("message"));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email/username and password")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam @NotBlank @Parameter(description = "Email or display name") String identifier,
            @RequestParam @NotBlank String password) {

        logger.info("Login attempt for identifier: {}", identifier);

        Map<String, Object> response = authService.login(identifier, password);

        if ((Boolean) response.get("success")) {
            logger.info("User successfully logged in: {}", identifier);
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Login failed for identifier: {} - {}", identifier, response.get("message"));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout current user")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                logger.info("User logged out: {}", username);
            } catch (Exception e) {
                logger.warn("Invalid token during logout attempt");
            }
        }

        Map<String, Object> response = authService.logout();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email address", description = "Verify user's email address using verification token")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam @NotBlank String token) {

        logger.info("Email verification attempt for token: {}", token.substring(0, 8) + "...");

        Map<String, Object> response = authService.verifyEmail(token);

        if ((Boolean) response.get("success")) {
            logger.info("Email verification successful for token: {}", token.substring(0, 8) + "...");
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Email verification failed for token: {} - {}", token.substring(0, 8) + "...",
                    response.get("message"));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email", description = "Send verification email again")
    public ResponseEntity<Map<String, Object>> resendVerificationEmail(
            @RequestParam @Email @NotBlank String email) {

        logger.info("Resend verification email request for: {}", email);

        Map<String, Object> response = authService.resendVerificationEmail(email);

        if ((Boolean) response.get("success")) {
            logger.info("Verification email resent successfully to: {}", email);
            return ResponseEntity.ok(response);
        } else {
            logger.warn("Failed to resend verification email to: {} - {}", email, response.get("message"));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user's information")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authorization header is missing or invalid");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid or expired token");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            Map<String, Object> response = authService.getCurrentUser(userId);

            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error getting current user", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid token");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/validate-token")
    @Operation(summary = "Validate JWT token", description = "Check if the provided JWT token is valid")
    public ResponseEntity<Map<String, Object>> validateToken(HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("valid", false);
            response.put("message", "Authorization header is missing or invalid");
            return ResponseEntity.ok(response);
        }

        try {
            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);

                response.put("valid", true);
                response.put("userId", userId);
                response.put("username", username);
                response.put("message", "Token is valid");
            } else {
                response.put("valid", false);
                response.put("message", "Token is invalid or expired");
            }

        } catch (Exception e) {
            logger.warn("Token validation failed", e);
            response.put("valid", false);
            response.put("message", "Token validation failed");
        }

        return ResponseEntity.ok(response);
    }
}